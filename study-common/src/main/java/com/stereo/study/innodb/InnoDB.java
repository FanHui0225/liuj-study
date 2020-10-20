package com.stereo.study.innodb;

import com.alibaba.innodb.java.reader.TableReader;
import com.alibaba.innodb.java.reader.TableReaderImpl;
import com.alibaba.innodb.java.reader.page.index.GenericRecord;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by liuj-ai on 2020/10/20.
 */
public class InnoDB {

    public static void main(String[] args) {
        String createTableSql = "CREATE TABLE `test` (\n" +
                "  `id` bigint(20) DEFAULT NULL,\n" +
                "  `a` int(11) DEFAULT NULL,\n" +
                "  `b` varchar(255) DEFAULT NULL,\n" +
                "  `c` bigint(20) DEFAULT NULL\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        String ibdFilePath = "D:\\IdeaProjects\\liuj-study\\study-common\\src\\main\\java\\com\\stereo\\study\\innodb\\test.ibd";
        try (TableReader reader = new TableReaderImpl(ibdFilePath, createTableSql)) {
            reader.open();

            // ~~~ query all records
            List<GenericRecord> recordList = reader.queryAll();
            for (GenericRecord record : recordList) {
                Object[] values = record.getValues();
                System.out.println(Arrays.asList(values));
                assert record.getPrimaryKey() == record.get("id");
            }

            // ~~~ query all records with filter, works like index condition pushdown
            Predicate<GenericRecord> predicate = r -> (int) (r.get("a")) == 3639730;
            List<GenericRecord> recordList2 = reader.queryAll(predicate);

            // ~~~ query all records with projection
            //[1, 2, null]
            //[2, 4, null]
            //[3, 6, null]
            //[4, 8, null]
            //[5, 10, null]
            //[3, 6, null]
            List<GenericRecord> recordList3 = reader.queryAll(ImmutableList.of("a"));
            for (GenericRecord record : recordList3) {
                Object[] values = record.getValues();
                System.out.println(Arrays.asList(values));
            }

            // ~~~ query all records with filter and projection
            List<GenericRecord> recordList4 = reader.queryAll(predicate, ImmutableList.of("a"));
            for (GenericRecord record : recordList4) {
                Object[] values = record.getValues();
                System.out.println(Arrays.asList(values));
            }
        }
    }
}
