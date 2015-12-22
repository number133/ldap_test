package service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class LdapServiceTest {

    @Before
    public void init() throws Exception {
        ApacheLDAPServer.getInstance().start();
        ApacheLDAPServer.getInstance().applyLdif(
                new File("target/test-classes/ldap/ldap-schema.ldif"));
        ApacheLDAPServer.getInstance().applyLdif(
                new File("target/test-classes/ldap/ldap-data.ldif"));
    }

    @After
    public void finalise() throws Exception {
        ApacheLDAPServer.getInstance().stop();
    }

    @Test
    public void testGetUserEmails(){
        // prepare
        LdapService ldapService = new LdapService();
        // call
        List<String> emailList = ldapService.getUserEmails();
        // verify
        assertEquals(2, emailList.size());
    }
}
