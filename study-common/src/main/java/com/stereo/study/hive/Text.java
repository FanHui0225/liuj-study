//package com.stereo.study.hive;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
///**
// * Created by liuj-ai on 2020/7/2.
// */
//public class Text {
//    public static void main(String[] args) throws IOException {
//
//        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("C:\\Users\\liuj-ai\\Desktop\\hdp\\table_revision.txt"), true));
//
//        String[] strings = {
//                "\"369265436656\",\"369265436653056\",\"building\",\"1591080166065\",\"0\",\"\",\"\",\"\",\"1587377863903\",\"2020/4/20 18:17:43\",\"\",\"6650986332147106757\",\"\",\"0\"",
//                "\"354844800614\",\"354844800614912\",\"collaboration_lock_main\",\"1587347282660\",\"1\",\"\",\"\",\"\",\"1587347282660\",\"2020/4/20 09:48:02\",\"\",\"6581107216236516098\",\"\",\"0\"",
//                "\"354844800617\",\"354844800614912\",\"floor\",\"1588243712908\",\"1588237886653\",\"\",\"\",\"\",\"1587347282660\",\"2020/4/20 09:48:02\",\"\",\"6581107216236516098\",\"\",\"0\"",
//                "\"354844800618\",\"354844800614912\",\"system\",\"1\",\"0\",\"\",\"\",\"\",\"1587347282660\",\"2020/4/20 09:48:02\",\"\",\"6581107216236516098\",\"\",\"0\"",
//                "\"354844800619\",\"354844800614912\",\"model_file\",\"0\",\"0\",\"\",\"\",\"\",\"1587347282660\",\"2020/4/20 09:48:02\",\"\",\"6581107216236516098\",\"\",\"0\"",
//                "\"369265436653\",\"369265436653056\",\"model_file_group\",\"1587377863903\",\"1\",\"\",\"\",\"\",\"1587377863903\",\"2020/4/20 18:17:43\",\"\",\"6650986332147106757\",\"\",\"0\"",
//                "\"369265436654\",\"369265436653056\",\"collaboration_lock_main\",\"1587377863903\",\"1\",\"\",\"\",\"\",\"1587377863903\",\"2020/4/20 18:17:43\",\"\",\"6650986332147106757\",\"\",\"0\"",
//                "\"369265436656\",\"369265436653056\",\"building\",\"1591080166065\",\"0\",\"\",\"\",\"\",\"1587377863903\",\"2020/4/20 18:17:43\",\"\",\"6650986332147106757\",\"\",\"0\"",
//                "\"369265436657\",\"369265436653056\",\"floor\",\"1591080166065\",\"0\",\"\",\"\",\"\",\"1587377863903\",\"2020/4/20 18:17:43\",\"\",\"6650986332147106757\",\"\",\"0\"",
//                "\"369265436658\",\"369265436653056\",\"system\",\"1\",\"0\",\"\",\"\",\"\",\"1587377863903\",\"2020/4/20 18:17:43\",\"\",\"6650986332147106757\",\"\",\"0\""
//        };
//
//        for (int j = 0; j < strings.length; j++) {
//            for (int i = 0; i < 1000000; i++) {
//                writer.newLine();
//                writer.write(strings[j]);
//            }
//        }
//
//        writer.close();
//    }
//}
