package ignitePersistance;

import cachestore.HelloWorldCacheStore;
import cachestore.TestMenuSystem;
import org.apache.ignite.*;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.logger.java.JavaLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.configuration.FactoryBuilder;
import java.util.Arrays;

/**
 * Created by debasish paul on 15-02-2018.
 */
public class IgniteClientNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(IgniteClientNode.class);

    /**
     * this is for testing purpose, do not run this class in prod env.
     *
     * @param args
     */
    public static void main(String[] args) {
        String ip = "";
        if (args.length>0){
            ip = args[0];
        }else {
            System.out.println("provide ip for client node.");
            System.exit(0);
        }

        Ignition.setClientMode(true);
        org.apache.ignite.configuration.IgniteConfiguration igniteConfiguration = new org.apache.ignite.configuration.IgniteConfiguration();

        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
        dataRegionConfiguration.setName("Default_Region");
//        dataRegionConfiguration.setMaxSize(4L * 1024 * 1024 * 1024);
        dataRegionConfiguration.setPersistenceEnabled(true);
        storageCfg.setDataRegionConfigurations(dataRegionConfiguration);
        igniteConfiguration.setDataStorageConfiguration(storageCfg);


        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Arrays.asList(ip+":47500..47502"));
        tcpDiscoverySpi.setIpFinder(ipFinder);
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);

        IgniteLogger log = new JavaLogger(true);
        igniteConfiguration.setGridLogger(log);


        Ignite ignite = Ignition.start(igniteConfiguration);
        IgniteCluster cluster = ignite.cluster();
        cluster.active();
        LOGGER.info(">>>>>>>>>>>>>>>>Ignite Cache Started successfully");

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
