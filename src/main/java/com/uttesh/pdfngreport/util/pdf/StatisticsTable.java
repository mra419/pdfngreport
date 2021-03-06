/*
 * Copyright 2015 Uttesh Kumar T.H..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uttesh.pdfngreport.util.pdf;

import com.uttesh.pdfngreport.common.Constants;
import com.uttesh.pdfngreport.handler.PdfReportHandler;
import com.uttesh.pdfngreport.model.ResultMeta;
import com.uttesh.pdfngreport.util.PDFCache;
import com.uttesh.pdfngreport.util.PdfngUtil;
import com.uttesh.pdfngreport.util.xml.ColumnHeader;
import com.uttesh.pdfngreport.util.xml.Row;
import com.uttesh.pdfngreport.util.xml.RowMeta;
import com.uttesh.pdfngreport.util.xml.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author Uttesh Kumar T.H.
 */
public class StatisticsTable {

    int passed = 0;
    int skipped = 0;
    int failed = 0;
    double percent = 0;
    String reportLocation = PdfngUtil.getReportLocation();

    public void populateData(Map<String, ResultMeta> result, Table statisticsTable) throws IOException {
        List<ColumnHeader> columns = new ArrayList<ColumnHeader>();
        String[] names = Constants.STATISTIC_TABLE_COLUMS;
        PdfReportHandler pdfReportHandler = new PdfReportHandler();
        String chartDisplay = "show";
        if (System.getProperty(Constants.SystemProps.REPORT_CHART_PROP) != null) {
            chartDisplay = System.getProperty(Constants.SystemProps.REPORT_CHART_PROP);
            if (chartDisplay == null || chartDisplay.trim().length() == 0) {
                chartDisplay = (String) PDFCache.get(Constants.SystemProps.REPORT_CHART_PROP);
            }
        }

        for (String className : result.keySet()) {
            ResultMeta resultMeta = result.get(className);
            passed = passed + resultMeta.getPassedSet().size();
            skipped = skipped + resultMeta.getSkippedSet().size();
            failed = failed + resultMeta.getFailedSet().size();
            double total = passed + skipped + failed;
            percent = ((double) passed / total) * 100;
            percent = Math.round(percent * 100) / 100.0d;
        }
        populateColumnHeader(columns, names, Constants.STATISTIC_TABLE_HEADER_COLOR);
        List<Row> rows = new ArrayList<Row>();
        RowMeta rowMeta = new RowMeta();
        
        rowMeta.setPassed("" + passed);
        rowMeta.setSkipped("" + skipped);
        rowMeta.setFailed("" + failed);
        rowMeta.setPercentage("" + percent + "%");
        rowMeta.setTableName("Statistics");
        Row row = new Row();
        row.setRowMeta(rowMeta);
        rows.add(row);
        statisticsTable.setColumnHeader(columns);
        statisticsTable.setRow(rows);
        statisticsTable.setTableName("Statistics");
        statisticsTable.setTableHeaderColor("#0079B6");
        statisticsTable.setReportLocation(reportLocation);
        
        if (chartDisplay.equalsIgnoreCase("show")) {
            DefaultPieDataset dataSet = new DefaultPieDataset();
            dataSet.setValue("Failed", failed);
            dataSet.setValue("Skipped", skipped);
            dataSet.setValue("Passed", passed);
            pdfReportHandler.generateChart(dataSet);
        }
    }

    public static void populateColumnHeader(List<ColumnHeader> columns, String[] names, String color) {
        ColumnHeader columnHeader = null;
        for (String name : names) {
            columnHeader = new ColumnHeader();
            columnHeader.setColorCode(color);
            columnHeader.setName(name);
            columns.add(columnHeader);
        }
    }

}
