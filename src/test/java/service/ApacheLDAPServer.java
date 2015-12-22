package service;

import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapService;
import org.apache.directory.server.protocol.shared.SocketAcceptor;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;

import java.io.File;

public class ApacheLDAPServer {

    private LdapService ldapService;
    private DirectoryService directoryService;
    private Partition partition;
    int port = 9389;
    private File workDir;

    private static ApacheLDAPServer instance;

    public static ApacheLDAPServer getInstance() {
        if (instance == null) {
            instance = new ApacheLDAPServer();
        }
        return instance;
    }

    private ApacheLDAPServer() {
    }

    /**
     * Starts server
     * @throws Exception
     */
    public void start() throws Exception {
        directoryService = new DefaultDirectoryService();
        directoryService.setAllowAnonymousAccess(true);
        directoryService.setAccessControlEnabled(false);
        directoryService.setShutdownHookEnabled( false );
        directoryService.getChangeLog().setEnabled(false);

        workDir = new File(System.getProperty("java.io.tmpdir"), "apache-ds");
        if(workDir.exists()){
            deleteDirSafe(workDir);
        }
        createDirSafe(workDir);
        directoryService.setWorkingDirectory(workDir);

        partition = addPartition("testPartition", "dc=test,dc=kz");

        SocketAcceptor socketAcceptor = new SocketAcceptor(null);

        ldapService = new LdapService();
        ldapService.setIpPort(port);
        ldapService.setSocketAcceptor(socketAcceptor);
        ldapService.setDirectoryService(directoryService);

        directoryService.startup();
        ldapService.start();
    }

    /**
     * Applies LDIF file
     * @param ldifFile LDIF file
     * @throws Exception
     */
    public void applyLdif(File ldifFile) throws Exception {
        new LdifFileLoader(directoryService.getAdminSession(), ldifFile, null).execute();
    }


    /**
     * Stops server
     * @throws Exception
     */
    public void stop() throws Exception {
        ldapService.stop();
        directoryService.shutdown();
        deleteDirSafe(workDir);
    }


    /**
     * Add a new partition to the server
     *
     * @param partitionId The partition Id
     * @param partitionDn The partition DN
     * @return The newly added partition
     * @throws Exception If the partition can't be added
     */
    private Partition addPartition( String partitionId, String partitionDn ) throws Exception
    {
        Partition partition = new JdbmPartition();
        partition.setId( partitionId );
        partition.setSuffix( partitionDn );
        directoryService.addPartition( partition );

        return partition;
    }

    /**
     * Creates directory. Throws exception if directory was not created.
     * @param dir Directory location
     */
    private void createDirSafe(File dir){
        if(!dir.mkdir()){
            throw new RuntimeException("Couldn't create dir");
        }
    }

    /**
     * Deletes directory. Throws exception if directory was not deleted.
     * @param dir Directory location
     */
    private void deleteDirSafe(File dir){
        if(!deleteDirectory(dir)){
            throw new RuntimeException("Couldn't delete dir");
        }
    }

    /**
     * Deletes directory with content.
     * @param dir Directory location
     * @return result
     */
    private boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
