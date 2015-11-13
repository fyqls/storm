package storm.spout;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import backtype.storm.spout.Scheme;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import com.taobao.gecko.core.util.LinkedTransferQueue;
import com.taobao.metamorphosis.client.MetaClientConfig;
import com.taobao.metamorphosis.exception.MetaClientException;
import com.taobao.metamorphosis.utils.ZkUtils.ZKConfig;
import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.MetaMessageSessionFactory;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import com.taobao.metamorphosis.client.consumer.MessageListener;
import storm.base.ConfCheck;
import storm.base.MacroDef;
import storm.base.MetaMessageWrapper;
import storm.base.StringScheme;
import storm.xml.SpoutXml;

/**
 * @author blogchong
 * @Blog www.blogchong.com
 * @email blogchong@gmail.com
 * @QQ_G 191321336
 * @version 2014��11��15�� ����22:11:01
 */

@SuppressWarnings("serial")
public class MetaSpout implements IRichSpout {

	public static final String FETCH_MAX_SIZE = "meta.fetch.max_size";

	public static final String TOPIC = "meta.topic";

	public static final int DEFAULT_MAX_SIZE = 128 * 1024;

	private transient MessageConsumer messageConsumer;

	private transient MessageSessionFactory sessionFactory;

	private MetaClientConfig metaClientConfig;

	private ConsumerConfig consumerConfig;

	static final Log log = LogFactory.getLog(MetaSpout.class);

	public static final long WAIT_FOR_NEXT_MESSAGE = 1L;

	private transient ConcurrentHashMap<Long, MetaMessageWrapper> id2wrapperMap;

	private transient SpoutOutputCollector collector;

	private transient LinkedTransferQueue<MetaMessageWrapper> messageQueue;

	private long spout_debug = 5000;
	private long register = 0;
	private long reg_tmp = 0;
	private boolean spout_flag = true;

	String topic = "storm-test";

	private final Scheme scheme = new StringScheme();

	private boolean flag_par = true;

	private String spoutXml = "MetaSpout.xml";

	//�Ƿ�������ñ�־λ
	private static boolean flag_load = false;

	@SuppressWarnings("rawtypes")
	private Map conf = null;

	public MetaSpout(String SpoutXml) {
		super();

		if (SpoutXml == null) {
			this.flag_par = false;
		} else {
			this.spoutXml = SpoutXml;
		}
	}

	@SuppressWarnings("rawtypes")
	public void open(final Map conf, final TopologyContext context,
			final SpoutOutputCollector collector) {
		System.out.println("MetaSpout	--	Start!");

		this.collector = collector;
		this.conf = conf;

		this.spout_debug = MacroDef.SPOUT_DEBUG;
		this.spout_flag = MacroDef.SPOUT_FLAG;

		this.reg_tmp = this.spout_debug;

		if (this.flag_par == false) {
			System.out
					.println("MetaSpout-- Erre: can't get the path of Spout.xml!");
		} else {
			// ���ü���߳�
			new ConfCheck(this.spoutXml, MacroDef.HEART_BEAT,
					MacroDef.Thread_type_metaqspout).start();
		}
	}

	private void setUpMeta(final String topic, final Integer maxSize)
			throws MetaClientException {

		this.sessionFactory = new MetaMessageSessionFactory(
				this.metaClientConfig);

		this.messageConsumer = this.sessionFactory
				.createConsumer(this.consumerConfig);

		this.messageConsumer.subscribe(topic, maxSize, new MessageListener() {

			public void recieveMessages(final Message message) {

				final MetaMessageWrapper wrapper = new MetaMessageWrapper(
						message);

				MetaSpout.this.id2wrapperMap.put(message.getId(), wrapper);

				MetaSpout.this.messageQueue.offer(wrapper);

				try {
					wrapper.latch.await();
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				// ��ȡ����ʧ��
				if (!wrapper.success) {
					throw new RuntimeException("MetaSpout	--	Obtain data fail!");
				}

			}

			public Executor getExecutor() {
				return null;
			}
		}).completeSubscribe();
	}

	public void close() {

		try {
			this.messageConsumer.shutdown();
		} catch (final MetaClientException e) {
			log.error("Shutdown consumer failed", e);
		}
		try {
			this.sessionFactory.shutdown();
		} catch (final MetaClientException e) {
			log.error("Shutdown session factory failed", e);
		}
	}

	//���ı�־λ
	public static void isload() {
		flag_load = false;
	}

	// ���ز�������
	public void Loading() {

		// MetaSpout����
		new SpoutXml(this.spoutXml).read();
		// ��ȡ����Topic
		String MetaRevTopic = SpoutXml.MetaRevTopic;
		// ��ȡMetaQ����zk��ַ
		String MetaZkConnect = SpoutXml.MetaZkConnect;
		// ��ȡMetaQ���ڵ�zk����
		String MetaZkRoot = SpoutXml.MetaZkRoot;
		// ��ȡmetaqconsumer���á�strom-test01��
		String MetaConsumerGroup = SpoutXml.MetaConsumerConf;

		// ��ȡ����zk����(metaq)
		ZKConfig zkconf = new ZKConfig();
		// zkconf.zkConnect // ="192.168.1.154:2181";//
		zkconf.zkConnect = MetaZkConnect;
		// zkconf.zkRoot = "/meta";
		zkconf.zkRoot = MetaZkRoot;

		MetaClientConfig metaconf = new MetaClientConfig();
		metaconf.setZkConfig(zkconf);

		this.metaClientConfig = metaconf;
		this.consumerConfig = new ConsumerConfig(MetaConsumerGroup);
		this.topic = MetaRevTopic;

		if (this.topic == null) {
			throw new IllegalArgumentException(TOPIC + " is null");
		}

		Integer maxSize = (Integer) conf.get(FETCH_MAX_SIZE);

		if (maxSize == null) {
			log.warn("Using default FETCH_MAX_SIZE");
			maxSize = DEFAULT_MAX_SIZE;
		}

		this.id2wrapperMap = new ConcurrentHashMap<Long, MetaMessageWrapper>();
		this.messageQueue = new LinkedTransferQueue<MetaMessageWrapper>();

		try {
			this.setUpMeta(this.topic, maxSize);
		} catch (final MetaClientException e) {
			log.error("Setup meta consumer failed", e);
		}
	}

	// ���ݷ�������
	public void nextTuple() {

		if (this.flag_par == false) {
			// �����ļ��в���Ϊ��(�޷��������ļ��л�ȡ��ȷ����)
			System.out
					.println("MetaSpout-- Erre: can't get the path of Spout.xml!");

		} else {

			// ��������ļ��Ƿ����
			if (flag_load == false) {
				// �����ļ�������������м��ز�������
				Loading();
				if (register != 0) {
					System.out.println("MetaSpout-- Conf Change: "
							+ this.spoutXml);
				} else {
					System.out.println("MetaSpout-- Conf Loaded: "
							+ this.spoutXml);
				}
			}

			if (this.messageConsumer != null) {
				try {

					final MetaMessageWrapper wrapper = this.messageQueue.poll(
							WAIT_FOR_NEXT_MESSAGE, TimeUnit.MILLISECONDS);

					if (wrapper == null) {
						return;
					}

					final Message message = wrapper.message;

					// ͳ�������������������ӡ
					this.register++;
					if (this.register >= this.reg_tmp) {

						if (this.spout_flag == true) {

							System.out
									.println("MetaSpout	--	Send Tuple Count: "
											+ this.register);
						}

						this.reg_tmp = this.register + this.spout_debug;

					}

					// ���ݷ�������
					this.collector.emit(
							this.scheme.deserialize(message.getData()),
							message.getId());

				} catch (final InterruptedException e) {
				}
			}
		}
	}

	public void ack(final Object msgId) {

		if (msgId instanceof Long) {

			final long id = (Long) msgId;

			final MetaMessageWrapper wrapper = this.id2wrapperMap.remove(id);

			if (wrapper == null) {

				System.out.println("MetaSpout--ack");
				log.warn(String.format("don't know how to ack(%s: %s)", msgId
						.getClass().getName(), msgId));
				return;

			}

			wrapper.success = true;
			wrapper.latch.countDown();
		} else {

			System.out.println("MetaSpout--ack");
			log.warn(String.format("don't know how to ack(%s: %s)", msgId
					.getClass().getName(), msgId));

		}

	}

	public void fail(final Object msgId) {

		if (msgId instanceof Long) {

			final long id = (Long) msgId;

			final MetaMessageWrapper wrapper = this.id2wrapperMap.remove(id);

			if (wrapper == null) {

				System.out.println("MetaSpout--fail");
				log.warn(String.format("don't know how to reject(%s: %s)",
						msgId.getClass().getName(), msgId));
				return;

			}
			wrapper.success = false;
			wrapper.latch.countDown();
		} else {

			System.out.println("MetaSpout--fail");
			log.warn(String.format("don't know how to reject(%s: %s)", msgId
					.getClass().getName(), msgId));

		}
	}

	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(this.scheme.getOutputFields());
	}

	public boolean isDistributed() {
		return true;
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}