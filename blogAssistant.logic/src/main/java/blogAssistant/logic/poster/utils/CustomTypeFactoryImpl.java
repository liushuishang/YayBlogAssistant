package blogAssistant.logic.poster.utils;

import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.TypeSerializer;
import org.apache.xmlrpc.serializer.TypeSerializerImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class CustomTypeFactoryImpl extends TypeFactoryImpl {

    public CustomTypeFactoryImpl(XmlRpcController pController) {
        super(pController);
    }

    private static final TypeSerializer STRING_SERIALIZER = new TypeSerializerImpl() {
        public static final String STRING_TAG = "string";
        @Override
        public void write(ContentHandler pHandler, Object pObject)
                throws SAXException {
            write(pHandler, STRING_TAG, pObject.toString());
        }

    };



    @Override
    public TypeSerializer getSerializer(XmlRpcStreamConfig pConfig,
                                        Object pObject) throws SAXException {
        if (pObject instanceof String) {
            return STRING_SERIALIZER;
        }else {
            return super.getSerializer(pConfig, pObject);
        }
    }
}
