package kylin.odoo.client.xmlrpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OdooXmlRpcClientTest {

    String host = "127.0.0.1";
    int port = 8069;
    String instance = "recipe";
    String username = "admin";
    String password = "admin";
    String object = "res.users";
    int testId = 0;
    int createdId = 0;
    OdooXmlRpcClient client = null;

    @org.junit.Before
    public void setUp() throws Exception {
        client = new OdooXmlRpcClient(host, port, instance, username, password);
        client.connect();
        testId = createObject();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        removeObject(testId);
        removeObject(createdId);
        client = null;
    }

    @org.junit.Test
    public void testConnect() throws Exception {
        client = new OdooXmlRpcClient(host, port, instance, username, password);
        int uid = client.connect();
        assertTrue(uid > 0);
    }

    @org.junit.Test
    public void testCreate() throws Exception {
        Map values = new HashMap<String, Object>();
        values.put("name", "Created User Name");
        values.put("login", String.valueOf(System.currentTimeMillis()));

        createdId = client.create(object, values);
        assertTrue(createdId > testId);
    }

    @org.junit.Test
    public void testUnlink() throws Exception {
        Integer[] unlinkIds = new Integer[]{testId};
        boolean unlinked = client.unlink(object, unlinkIds);
        assertTrue(unlinked);
    }

    @org.junit.Test
    public void testWrite() throws Exception {
        Integer[] writeIds = new Integer[]{testId};
        Map writeValues = new HashMap<String, Object>();
        writeValues.put("name", "New Name");
        boolean writen = client.write(object, writeIds, writeValues);
        assertTrue(writen);
    }

    @org.junit.Test
    public void testSearch() throws Exception {
        Map filterMap = new HashMap<String, Object>();
        filterMap.put("name", "Test Name");
        Integer[] searchedIds = client.search(object, filterMap);
        assertTrue(searchedIds.length > 0);
    }

    @org.junit.Test
    public void testRead() throws Exception {
        Integer[] readIds = new Integer[]{testId};
        String[] readFields = new String[]{"name", "login"};
        List<Map<String, Object>> records = client.read(object, readIds, readFields);
        for (Map<String, Object> map : records) {
            assertEquals(map.get("name"), "Test Name");
            assertEquals(map.get("login"), "Test Login Name");
        }
    }

    private int createObject() throws Exception {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", "Test Name");
        values.put("login", "Test Login Name");
        int foodId = client.create(object, values);

        return foodId;
    }

    private void removeObject(int id) throws Exception {
        client.unlink(object, new Integer[]{id});
    }
}