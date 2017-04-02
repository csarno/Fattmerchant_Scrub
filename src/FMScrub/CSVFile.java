package FMScrub;

/**
 * Created by csarno on 7/11/16.
 */

import com.sun.deploy.util.ArrayUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSVFile {

    private ArrayList<String> csvRecords = new ArrayList<String>();
    private ArrayList<CSVRecord> wholeRecords = new ArrayList<CSVRecord>();
    private ArrayList<CSVRecord> recordsToBeRemoved = new ArrayList<>();
    private ArrayList<Integer> removeRecords = new ArrayList<>();

    public CSVFile(String inputFilePath, String outputFilePath) {
        Reader in = null;
        String mInputFile = inputFilePath;
        String mOutputFile = outputFilePath;
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;

        //private static final String [] FILE_HEADER_MAPPING = {"id","firstName","lastName","gender","age"};
        //New header added is_manual (between total and success, customer_notes (between customer_address_zip and customer_created_at)
        String[] FILE_HEADER_MAPPING3 = {"id",
                "type",
                "merchant_id",
                "user_id",
                "customer_id",
                "total",
                "subtotal",
                "tax",
                "is_manual",
                "success",
                "memo",
                "reference",
                "meta",
                "pre_auth",
                "last_four",
                "created_at",
                "updated_at",
                "payment_method",
                "payment_nickname",
                "payment_person_name",
                "payment_card_type",
                "payment_last_four",
                "payment_card_exp",
                "payment_bank_name",
                "payment_bank_type",
                "payment_bank_holder_type",
                "billing_address_1",
                "billing_address_2",
                "billing_address_city",
                "billing_address_state",
                "billing_address_zip",
                "customer_firstname",
                "customer_lastname",
                "customer_company",
                "customer_email",
                "customer_phone",
                "customer_address_1",
                "customer_address_2",
                "customer_address_city",
                "customer_address_state",
                "customer_address_zip",
                "customer_notes",
                "customer_reference",
                "customer_created_at",
                "customer_updated_at",
                "customer_deleted_at",
                "user_name",
                "system_admin",
                "user_created_at",
                "user_updated_at",
                "user_deleted_at",
                "total_refunded",
                "is_voided"};


        try {
            in = new FileReader(mInputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Iterable<CSVRecord> records = null;
        CSVFormat csvFileFormat = CSVFormat.EXCEL.withHeader(FILE_HEADER_MAPPING3);
        CSVParser records = null;
        try {
            records = new CSVParser(in, csvFileFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (CSVRecord record : records) {
            String id = record.get("id");
            String total = record.get("total");
            String company = record.get("customer_company");
            String success = record.get("success");
            String type = record.get("type");
            System.out.printf("id: %s\n", id);
            System.out.printf("company: %s\n", company);
            System.out.printf("total: %s\n", total);
            System.out.printf("Success? %s\n", success);
            System.out.printf("Type: %s\n\n", type);
            if (Objects.equals(success, "1") && Objects.equals(type, "charge")) {
                csvRecords.add(record.get("customer_company"));
                wholeRecords.add(record);
            }
            if (Objects.equals(success, "1") && Objects.equals(type, "refund")) {
                csvRecords.add(record.get("customer_company"));
                wholeRecords.add(record);
            }
            if (Objects.equals(success, "1") && Objects.equals(type, "void")) {
                System.out.println("Void found!");
                markTransactionForRemoval(record);
            }
        }

        System.out.printf("Record: %s\n", records.toString());
        System.out.printf("Number of records: %s\n", csvRecords.size());

        //Remove marked transactions prior to writing output file
        removeMarkedTransactions();

        int counter = 0;
        for (String successRecord : csvRecords) {
            counter++;
            System.out.printf("Successful transaction %d: %s\n", counter, successRecord);
        }



        //String fileName = "res/output.csv";
        //initialize FileWriter object
        try {
            System.out.printf("Outfile: %s", mOutputFile);
            fileWriter = new FileWriter(mOutputFile);
            //initialize CSVPrinter object
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            //Create CSV file header
            for (CSVRecord record : wholeRecords) {
                csvFilePrinter.printRecord(record);
            }
            fileWriter.flush();
            fileWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void markTransactionForRemoval(CSVRecord record) {
        recordsToBeRemoved.add(record);
        System.out.printf("Marked for removal: %s\n\n", record.get("meta"));
    }

    private void removeMarkedTransactions() {
        /*
        for (CSVRecord record : wholeRecords) {
            for (CSVRecord markedRecord : recordsToBeRemoved) {
                String currentCompany = record.get("customer_company");
                if (markedRecord.get("customer_company").equals(record.get("customer_company")) &&
                        markedRecord.get("total").equals("-" + record.get("total")) &&
                        markedRecord.get("meta").equals(record.get("meta")) &&
                        record.get("type").equals("charge")) {
                    System.out.printf("Voided transaction: %s\n", currentCompany);
                    long position = record.getRecordNumber();
                    wholeRecords.remove(((int) position));

                }

            }
        } */
        //System.out.printf("Removed: %b\n", wholeRecords.contains(recordsToBeRemoved));
        //wholeRecords.removeAll(recordsToBeRemoved);
        System.out.println("Checking for voided transactions...");
        int listPos = 0;
        for (CSVRecord record : wholeRecords) {
            for (CSVRecord markedRecord : recordsToBeRemoved) {
                String recordMeta = record.get("meta");
                String markedMeta = markedRecord.get("meta");
                String recordTotal = record.get("total");
                String markedTotal = markedRecord.get("total");
                String recordCustId = record.get("customer_id");
                String markedCustId = markedRecord.get("customer_id");
                System.out.printf("Record meta: %s\nMarked meta: %s\nRecord total: %s, Marked total: %s\n\n", recordMeta, markedMeta, recordTotal, markedTotal);
                if(recordMeta.equalsIgnoreCase(recordMeta) && recordCustId.equalsIgnoreCase(markedCustId)) {
                    System.out.printf("Record to be deleted: %s\n", markedRecord.get("customer_company"));
                    removeRecords.add(listPos);
                }
            }
            listPos++;
        }
        System.out.printf("Size before: %d\n", wholeRecords.size());
        for (Integer i : removeRecords) {
            System.out.printf("REMOVE: %s\n", i);
            CSVRecord currRecord = wholeRecords.get(i);
            System.out.printf("Record being removed: %s\n", currRecord.get("customer_company"));
            wholeRecords.remove((int)i);
        }
        System.out.printf("Size after: %d\n", wholeRecords.size());
    }

}