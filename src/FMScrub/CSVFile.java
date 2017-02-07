package FMScrub;

/**
 * Created by csarno on 7/11/16.
 */

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSVFile {

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

        ArrayList<String> csvRecords = new ArrayList<String>();
        ArrayList<CSVRecord> wholeRecords = new ArrayList<CSVRecord>();

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
            System.out.printf("Success? %s\n\n", success);
            if (Objects.equals(success, "1") && Objects.equals(type, "charge")) {
                csvRecords.add(record.get("customer_company"));
                wholeRecords.add(record);
            }
            if (Objects.equals(success, "1") && Objects.equals(type, "refund")) {
                csvRecords.add(record.get("customer_company"));
                wholeRecords.add(record);
            }
        }

        System.out.printf("Record: %s\n", records.toString());
        System.out.printf("Number of records: %s\n", csvRecords.size());

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

}
