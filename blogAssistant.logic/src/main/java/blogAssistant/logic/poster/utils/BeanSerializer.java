package blogAssistant.logic.poster.utils;

import blogAssistant.logic.common.utils.MapUtils;
import org.apache.xmlrpc.common.TypeFactory;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.MapSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.Map;

/**
 * Created by yuananyun on 2016/8/20.
 */
public class BeanSerializer extends MapSerializer {
    /**
     * Creates a new instance.
     *
     * @param pTypeFactory The factory being used for creating serializers.
     * @param pConfig      The configuration being used for creating serializers.
     */
    public BeanSerializer(TypeFactory pTypeFactory, XmlRpcStreamConfig pConfig) {
        super(pTypeFactory, pConfig);
    }

    @Override
    public void write(ContentHandler pHandler, Object pObject) throws SAXException {
        Map mObject = MapUtils.transBean2Map(pObject);
        super.write(pHandler, mObject);
    }
}
