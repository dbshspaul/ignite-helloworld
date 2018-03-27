package ignitePersistance;

import cachestore.HelloWorldCacheStore;
import cachestore.TestMenuSystem;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.configuration.FactoryBuilder;
import java.util.Arrays;

public class PersistentStoreNodeStartup {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentStoreNodeStartup.class);

    public static void main(String[] args) throws Exception {
        String ip = "";
        if (args.length>0){
            ip = args[0];
        }else {
            System.out.println("provide ip for Persistent node.");
            System.exit(0);
        }
        IgniteConfiguration cfg = new IgniteConfiguration();

        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        storageCfg.getDefaultDataRegionConfiguration().setPersistenceEnabled(true);
        cfg.setDataStorageConfiguration(storageCfg);

        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Arrays.asList(ip+":47500..47502"));
        tcpDiscoverySpi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(tcpDiscoverySpi);

        Ignite ignite = Ignition.start(cfg);
        LOGGER.info(">>>>>>>>>>>>>>>>Ignite  Persistent Store Node Started successfully");

        IgniteCluster cluster = ignite.cluster();
        cluster.active(true);
        CacheConfiguration contractCacheConfig = new CacheConfiguration();
        contractCacheConfig.setName("hello-world");
        contractCacheConfig.setBackups(1);
        contractCacheConfig.setCacheMode(CacheMode.PARTITIONED);
        contractCacheConfig.setCacheStoreFactory(FactoryBuilder.factoryOf(HelloWorldCacheStore.class));
        IgniteCache cache = ignite.getOrCreateCache(contractCacheConfig);
        LOGGER.info("hello-world server cache created.");
        TestMenuSystem.menuSystem(cache);
    }


}
