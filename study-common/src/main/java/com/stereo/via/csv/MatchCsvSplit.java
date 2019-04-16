package com.stereo.via.csv;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuj-ai on 2018/8/28.
 */
public class MatchCsvSplit {

    private static volatile long index = 0;

    private final static long splitLen = 50 * 10000;

    private final static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void split() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        Iterator<DataLine> iterator = new CsvToBeanBuilder(new FileReader("C:\\Users\\liuj-ai\\Desktop\\阿里测试数据\\fresh_comp_offline\\tianchi_fresh_comp_train_user.csv"))
            .withType(DataLine.class).build().iterator();
        List<DataLine> dataLines = new ArrayList<>();
        long count = 0;
        DataLine dataLine = null;
        boolean isRH = false;
        for (; iterator.hasNext(); dataLine = iterator.next()) {
            if (!isRH) {
                isRH = true;
                continue;
            }
            count++;
            dataLines.add(dataLine);
            if (count % splitLen == 0) {
                toCsv(dataLines);
            }
        }
        if (dataLines.size() > 0) {
            toCsv(dataLines);
        }
    }

    private static void toCsv(
        List<DataLine> dataLines) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        final long execIndex = ++index;
        final List<DataLine> execDataLines = new ArrayList<>(dataLines);
        executor.execute(new Runnable() {
            @Override public void run() {
                //ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
                //strat.setType(DataLine.class);
                //strat.setColumnMapping(new String[] {"user_id", "item_id", "behavior_type","user_geohash","item_category","time"});
                try {
                    Writer writer = new FileWriter("C:\\Users\\liuj-ai\\Desktop\\阿里测试数据\\fresh_comp_offline\\基础数据拆分\\tianchi_fresh_comp_train_user_" + execIndex + ".csv");
                    StatefulBeanToCsv<DataLine> beanToCsv = new StatefulBeanToCsvBuilder<DataLine>(writer).withOrderedResults(true).build();//.withMappingStrategy(strat).build();
                    beanToCsv.write(execDataLines);
                    writer.close();
                } catch (Exception ex) {

                }
            }
        });
        dataLines.clear();
    }

    public static void main(
        String[] args) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        split();
    }
}
