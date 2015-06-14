package kylin.odoo.client.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfig;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OdooXmlRpcClient {

    private static final String LOGIN_PATH = "/xmlrpc/common";
    private static final String EXECUTION_PATH = "/xmlrpc/object";
    private static final String CREATE = "create";
    private static final String UNLINK = "unlink";
    private static final Object WRITE = "write";
    private static final String SEARCH = "search";
    private static final Object READ = "read";

    private String protocol = "http";
    private String host = "127.0.0.1";
    private int port = 8069;
    private String instance = "odoo";
    private String username = "admin";
    private String password = "admin";
    private int uid = -1;

    XmlRpcClient client = new XmlRpcClient();

    public OdooXmlRpcClient(String host, int port, String instance, String login, String password) {
        this.host = host;
        this.port = port;
        this.instance = instance;
        this.username = login;
        this.password = password;
    }

    private XmlRpcClientConfig getLoginConfig() throws MalformedURLException {
        XmlRpcClientConfigImpl xmlRpcConfigLogin = new XmlRpcClientConfigImpl();
        xmlRpcConfigLogin.setEnabledForExtensions(true);
        URL loginURL = new URL(protocol, host, port, LOGIN_PATH);
        xmlRpcConfigLogin.setServerURL(loginURL);
        return xmlRpcConfigLogin;
    }

    private XmlRpcClientConfig getExecutionConfig() throws MalformedURLException {
        XmlRpcClientConfigImpl xmlRpcConfigLogin = new XmlRpcClientConfigImpl();
        xmlRpcConfigLogin.setEnabledForExtensions(true);
        URL loginURL = new URL(protocol, host, port, EXECUTION_PATH);
        xmlRpcConfigLogin.setServerURL(loginURL);
        return xmlRpcConfigLogin;
    }

    public int connect() {
        try {
            XmlRpcClientConfig loginConfig = getLoginConfig();
            client.setConfig(loginConfig);
            Object[] params = new Object[]{instance, username, password};
            Object id = client.execute("login", params);
            if (id instanceof Integer) {
                uid = (Integer) id;
                return uid;
            }
            return -1;
        } catch (XmlRpcException e) {
            e.printStackTrace();
            return -2;
        } catch (Exception e) {
            e.printStackTrace();
            return -3;
        }
    }

    private Object[] getSearchFilters(Map<String, Object> filterMap){
        List<Object[]> filterList = new ArrayList<Object[]>();
        for(Map.Entry<String, Object> entry : filterMap.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            Object[] filter = new Object[]{key, "=", value};
            filterList.add(filter);
        }
        return filterList.toArray();
    }

    public Integer create(String object, Map<String, Object> valueMap) throws MalformedURLException, XmlRpcException {
        XmlRpcClientConfig executionConfig = getExecutionConfig();
        client.setConfig(executionConfig);
        Object[] params = new Object[]{instance, uid, password, object, CREATE, valueMap};
        Object result = client.execute("execute", params);
        if(result instanceof Integer){
            int id = (Integer)result;
            return id;
        }
        return -1;
    }

    public Boolean unlink(String object, Integer[] ids) throws MalformedURLException, XmlRpcException {
        XmlRpcClientConfig executionConfig = getExecutionConfig();
        client.setConfig(executionConfig);
        Object[] params = new Object[]{instance, uid, password, object, UNLINK, ids};
        Object result = client.execute("execute", params);
        if(result instanceof Boolean){
            return (Boolean)result;
        }
        return null;
    }

    public Boolean write(String object, Integer[] ids, Map<String, Object> valueMap) throws MalformedURLException, XmlRpcException {
        XmlRpcClientConfig executionConfig = getExecutionConfig();
        client.setConfig(executionConfig);
        Object[] params = new Object[]{instance, uid, password, object, WRITE, ids, valueMap, null};
        Object result = client.execute("execute", params);
        if(result instanceof Boolean){
            return (Boolean)result;
        }
        return null;
    }

    public Integer[] search(String object, Map<String, Object> filterMap) throws MalformedURLException, XmlRpcException {
        XmlRpcClientConfig executionConfig = getExecutionConfig();
        client.setConfig(executionConfig);
        Object[] filters = getSearchFilters(filterMap);
        Object[] params = new Object[]{instance, uid, password, object, SEARCH, filters, 0, 0, null};
        Object result = client.execute("execute", params);
        if(result instanceof Object[]){
            Object[] resultA = (Object[]) result;
            Integer[] ids = new Integer[resultA.length];
            for(int i = 0; i < resultA.length; i++ ){
                Object o = resultA[i];
                int id = (Integer)o;
                ids[i] = id;
            }
            return ids;
        }
        return null;
    }

    public List<Map<String, Object>> read(String object, Integer[] ids, String[] fields) throws MalformedURLException, XmlRpcException {
        XmlRpcClientConfig executionConfig = getExecutionConfig();
        client.setConfig(executionConfig);
        Object[] params = new Object[]{instance, uid, password, object, READ, ids, fields, null};
        Object result = client.execute("execute", params);
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        if(result instanceof Object[]) {
            for (Object row : (Object[]) result) {
                Map<String, Object> map = (HashMap<String, Object>) row;
                records.add(map);
            }
            return records;
        }
        return null;
    }
}