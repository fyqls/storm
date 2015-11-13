package storm.base;

import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author blogchong
 * @Blog   www.blogchong.com
 * @email  blogchong@gmail.com
 * @QQ_G   191321336
 * @version 2014��11��15�� ����21:36:49
 */

//�淶�����
@SuppressWarnings("serial")
public class StringScheme implements Scheme {

	public List<Object> deserialize(byte[] bytes) {

		try {
			//���ݵı��붨��
			return new Values(new String(bytes, MacroDef.ENCODING));

		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException(e);

		}
	}

	public Fields getOutputFields() {
		return new Fields("str");
	}
}