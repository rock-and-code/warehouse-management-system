package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.Invoice;
import com.example.warehouseManagement.Domains.DTOs.InvoiceDto;
import com.example.warehouseManagement.Domains.DTOs.MonthlySalesDto;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

    @Query(value = "SELECT \n" +
        "i.id AS \"id\", \n" +
        "i.date AS \"date\", \n" +
        "i.id AS \"invoiceNumber\", \n" +
        "ROUND(SUM(invoice_line.qty * item_price.price), 2) AS \"total\" \n" +
        "FROM invoice i \n" +
        "INNER JOIN invoice_line ON invoice_line.invoice_id = i.id \n" +
        "INNER JOIN item_price ON invoice_line.item_id = item_price.item_id \n" +
        "GROUP BY i. \n" +
        "ORDER BY \"date\"", nativeQuery = true)
        public List<InvoiceDto> findAllInvoices();

    @Query(value = "SELECT \n" +
        "i.id AS \"id\", \n" +
        "i.date AS \"date\", \n" +
        "i.id AS \"invoiceNumber\", \n" +
        "ROUND(SUM(invoice_line.qty * item_price.price), 2) AS \"total\" \n" +
        "FROM invoice i \n" +
        "INNER JOIN invoice_line ON invoice_line.invoice_id = i.id \n" +
        "INNER JOIN item_price ON invoice_line.item_id = item_price.item_id \n" +
        "INNER JOIN customer ON i.customer_id = customer.id \n" + //
        "WHERE i.customer_id = :customerId \n" +
        "GROUP BY i. \n" +
        "ORDER BY \"date\"", nativeQuery = true)
        public List<InvoiceDto> findAllInvoiceByCustomerId(@Param("customerId") Long customerId);

        @Query(value = "SELECT \n" +
        "i.id AS \"id\", \n" +
        "i.date AS \"date\", \n" +
        "i.id AS \"invoiceNumber\", \n" +
        "ROUND(SUM(invoice_line.qty * item_price.price), 2) AS \"total\" \n" +
        "FROM invoice i \n" +
        "INNER JOIN invoice_line ON invoice_line.invoice_id = i.id \n" +
        "INNER JOIN item_price ON invoice_line.item_id = item_price.item_id \n" +
        "WHERE so.date LIKE CONCAT(:year, '-', :month, '-', '0', '%') \n" + 
        "GROUP BY i. \n" +
        "ORDER BY \"date\"", nativeQuery = true)
        public List<InvoiceDto> findByYearAndMonth(@Param("year") int year,
        @Param("month") String month);

        @Query(value = " SELECT \n" +
            "FORMATDATETIME(i.date, 'MMM') AS \"Month\", \n" +
            "ROUND(SUM(invoice_line.qty * item_price.price), 2) AS \"Total\" \n" +
            "FROM invoice i \n" +
            "INNER JOIN invoice_line ON invoice_line.invoice_id = i.id \n" +
            "INNER JOIN item_price ON invoice_line.item_id = item_price.item_id \n" +
            "WHERE EXTRACT(MONTH FROM Date) >= EXTRACT(MONTH FROM CURRENT_DATE()) - 3 \n" +
            "AND EXTRACT(MONTH FROM Date) < EXTRACT(MONTH FROM CURRENT_DATE()) \n" +
            "GROUP BY FORMATDATETIME(i.date, 'MMM') \n" +
            "ORDER BY FORMATDATETIME(i.date, 'MMM') ASC", nativeQuery = true)
        public List<MonthlySalesDto> findLastThreeMonthsSales();

        /**
         SELECT
            i.id AS "id",
            i.date AS "date",
            i.id AS "invoiceNumber",
            ROUND(SUM(invoice_line.qty * item_price.price), 2) AS "total"
            FROM invoice i
            INNER JOIN invoice_line ON invoice_line.invoice_id = i.id
            INNER JOIN item_price ON invoice_line.item_id = item_price.item_id
            GROUP BY i.id
            ORDER BY "date"

         */
    
}
