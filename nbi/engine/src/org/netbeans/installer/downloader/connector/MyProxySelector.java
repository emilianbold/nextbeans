package org.netbeans.installer.downloader.connector;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.installer.utils.xml.DomExternalizable;
import org.netbeans.installer.utils.xml.DomUtil;
import org.netbeans.installer.utils.xml.visitors.DomVisitor;
import org.netbeans.installer.utils.xml.visitors.RecursiveDomVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Danila_Dugurov
 */
public class MyProxySelector extends ProxySelector implements DomExternalizable {
  
  private final Map<MyProxyType, MyProxy> proxies = new HashMap<MyProxyType, MyProxy>();
  
  public void add(MyProxy proxy) {
    proxies.put(proxy.type, proxy);
  }
  
  public void remove(MyProxyType type) {
    proxies.remove(type);
  }
  
  public MyProxy getForType(MyProxyType type) {
    return proxies.get(type);
  }
  
  public List<Proxy> select(URI uri) {
    if (uri == null) throw new IllegalArgumentException("uri:" + uri);
    Proxy proxy = Proxy.NO_PROXY;
    if ("http".equalsIgnoreCase(uri.getScheme()) ||
      "https".equalsIgnoreCase(uri.getScheme())) {
      if (proxies.containsKey(MyProxyType.HTTP))
        proxy = proxies.get(MyProxyType.HTTP).getProxy();
    } else if ("ftp".equalsIgnoreCase(uri.getScheme())){
      if (proxies.containsKey(MyProxyType.FTP))
        proxy = proxies.get(MyProxyType.FTP).getProxy();
    } else {
      if (proxies.containsKey(MyProxyType.SOCKS))
        proxy = proxies.get(MyProxyType.SOCKS).getProxy();
    }
    return Collections.singletonList(proxy);
  }
  
  public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
    //TODO: now very silly selector no any rerang!
  }
  
  public void readXML(Element element) {
    final DomVisitor visitor = new RecursiveDomVisitor() {
      public void visit(Element element) {
        if ("proxy".equals(element.getNodeName())) {
          final MyProxy proxy = new MyProxy();
          proxy.readXML(element);
          add(proxy);
        } else super.visit(element);
      }
    };
    visitor.visit(element);
  }
  
  public Element writeXML(Document document) {
    final Element root = document.createElement("selector-proxies");
    for (MyProxy proxy: proxies.values())
      DomUtil.addChild(root, proxy);
    return root;
  }
}
