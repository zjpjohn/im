package tigase.shiku;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tigase.conf.ConfigurationException;
import tigase.server.AbstractMessageReceiver;
import tigase.server.Packet;

/**
 * 参数配置组件
 * 
 * @author chais@www.camelot.com
 */
public class paramProperties extends AbstractMessageReceiver {

    private static final Logger log = Logger.getLogger(paramProperties.class.getCanonicalName());
    private static String xmpp_domain;

    public paramProperties() {
        super();
        setName("param-properties");
    }

    @Override
    public Map<String, Object> getDefaults(Map<String, Object> params) {
        return super.getDefaults(params);
    }

    // 获取配置文件中参数并赋值
    @Override
    public void setProperties(Map<String, Object> props) throws ConfigurationException {
        try {
            super.setProperties(props);
            xmpp_domain = (String) props.get("xmpp_domain");
        } catch (Exception e) {
            log.log(Level.WARNING, "******获取参数失败！！！" + e);
            throw new ConfigurationException("******获取参数失败！！！", e);
        }
    }

    @Override
    public void processPacket(Packet packet) {
        // TODO Auto-generated method stub

    }

    public String getXmpp_domain() {
        return xmpp_domain;
    }

    public void setXmpp_domain(String xmpp_domain) {
        this.xmpp_domain = xmpp_domain;
    }

}
