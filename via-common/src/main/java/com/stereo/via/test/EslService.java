//package com.stereo.via;
//
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHitField;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.aggregations.AggregationBuilder;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Map;
//
///**
// * Created by haoy on 2017/8/22.
// */
////@SpringBootApplication
////@RunWith(SpringJUnit4ClassRunner.class)
//public class EslService {
//
//    private static Logger logger = LoggerFactory.getLogger(EslService.class);
////    @Autowired
////    private ClienteRepository clienteDao;
////
////    @Autowired
////    private TriggerLogRepository triggerLogRepository;
//
//    //@Test
////    public void findCliente()
////    {
//////        String id = "11";
//////        Cliente cliente = clienteDao.findOne(id);
//////        logger.info(" get cliente by id {} is {}", id, cliente);
//////        //curl -XGET 'localhost:9200/logstash-octo-task-2017.08.24/octo-task/AV4TfpRNpa09AHZ_3AI4?pretty'
//////        String triggerId = "AV4TfpRNpa09AHZ_3AI4";
//////        TriggerLogDto TriggerLog = TriggerLogDao.findOne(triggerId);
//////        logger.info(" get TriggerLog by triggerId {} is {}", triggerId, TriggerLog);
////        //this.repository.deleteAll();
////        //saveCustomers();
////        fetchAllCustomers();
//////        fetchIndividualCustomers();
////    }
//
//    public static void main(String aa[])
//    {
//        findCount();
//    }
//
////    @Test
//    public static void findCount()
//    {
////        TransportClient client = null;
//        try {
//            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.129.57.79"), 9300));
//            customCount(false,"*:*",client);
//        } catch (Exception e) {
//            logger.info("========catch========>>"+e.getMessage());
//            e.printStackTrace();
//        }finally {
//            logger.info("========final========>>");
////            client.close();
//        }
//    }
//
//    private static long customCount(boolean isQueryAll, String queryString,TransportClient client) throws Exception {
//
//        try {
//
//            SearchRequestBuilder search = client.prepareSearch("*");
//            FilterAggregationBuilder filterAggregationBuilder =
//                    AggregationBuilders.filter("timestamp",QueryBuilders.rangeQuery("@timestamp").gte("2017-08-30T00:00:00.104Z").lte("2017-08-31T00:00:00.104Z"));
//            TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("count").field("msg_key");
//            filterAggregationBuilder.subAggregation(termsAggregationBuilder);
//            search.addAggregation(filterAggregationBuilder);
//            SearchResponse r = search.get();//得到查询结果
//            System.out.println(r);
////            SearchHits hits = r.getHits();
////            System.out.println(hits.getTotalHits());
////            int temp = 0;
////            for (int i = 0; i < hits.getHits().length; i++) {
////                // System.out.println(hits.getHits()[i].getSourceAsString());
////                System.out.print(hits.getHits()[i].getSource().get("product_id"));
////                // if(orderfield!=null&&(!orderfield.isEmpty()))
////                // System.out.print("\t"+hits.getHits()[i].getSource().get(orderfield));
////                System.out.print("\t"
////                        + hits.getHits()[i].getSource().get("category_id"));
////                System.out.print("\t"
////                        + hits.getHits()[i].getSource().get("category_name"));
////                System.out.println("\t"
////                        + hits.getHits()[i].getSource().get("name"));
////            }
//            return r.getHits().getTotalHits();
//        }catch (Exception e){
//            logger.error("统计日期数量出错！",e);
//        }
//        return 0;
//    }
//}
