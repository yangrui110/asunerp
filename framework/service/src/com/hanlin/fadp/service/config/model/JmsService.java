
package com.hanlin.fadp.service.config.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;jms-service&gt;</code> element.
 */
@ThreadSafe
public final class JmsService {

    private final String name;
    private final String sendMode;
    private final List<Server> servers;

    JmsService(Element jmsServiceElement) throws ServiceConfigException {
        String name = jmsServiceElement.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new ServiceConfigException("<jms-service> element name attribute is empty");
        }
        this.name = name;
        String sendMode = jmsServiceElement.getAttribute("send-mode").intern();
        if (sendMode.isEmpty()) {
            sendMode = "none";
        }
        this.sendMode = sendMode;
        List<? extends Element> serverElementList = UtilXml.childElementList(jmsServiceElement, "server");
        if (serverElementList.isEmpty()) {
            this.servers = Collections.emptyList();
        } else {
            List<Server> servers = new ArrayList<Server>(serverElementList.size());
            for (Element serverElement : serverElementList) {
                servers.add(new Server(serverElement));
            }
            this.servers = Collections.unmodifiableList(servers);
        }
    }

    public String getName() {
        return name;
    }

    public String getSendMode() {
        return this.sendMode;
    }

    public List<Server> getServers() {
        return this.servers;
    }
}
