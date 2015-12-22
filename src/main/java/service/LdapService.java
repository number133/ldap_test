package service;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

public class LdapService {

    public List<String> getUserEmails(){
        LdapTemplate ldapTemplate = createTemplate();
        LdapQuery query = query()
                .base("").attributes(new String[]{"*", "+"})
                .where("objectClass").is("customEmployee");
        List<String> result = ldapTemplate.search(query,
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs)
                            throws NamingException {
                        return (String)attrs.get("mail").get();
                    }
                }
        );
        return result;
    }

    private LdapTemplate createTemplate(){
        LdapContextSource ctxSrc = new LdapContextSource();
        ctxSrc.setUrl("ldap://localhost:9389");
        ctxSrc.setBase("dc=test,dc=kz");
        ctxSrc.setUserDn("cn=TestName1 TestLast1,l=astana,ou=users,dc=test,dc=kz");
        ctxSrc.setPassword("secret");
        ctxSrc.afterPropertiesSet();
        return new LdapTemplate(ctxSrc);
    }
}
