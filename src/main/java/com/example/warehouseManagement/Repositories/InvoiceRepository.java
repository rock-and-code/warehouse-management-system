package com.example.warehouseManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.warehouseManagement.Domains.Invoice;
import com.example.warehouseManagement.Domains.DTOs.InvoiceDto;
import com.example.warehouseManagement.Domains.DTOs.MonthlySalesDto;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

    @Query(value = """
        SELECT\s
        i.id AS "id",\s
        i.date AS "date",\s
        i.id AS "invoiceNumber",\s
        ROUND(SUM(invoice_line.qty * item_price.price), 2) AS "total"\s
        FROM invoice i\s
        INNER JOIN invoice_line ON invoice_line.invoice_id = i.id\s
        INNER JOIN item_price ON invoice_line.item_id = item_price.item_id\s
        GROUP BY i.\s
        ORDER BY "date"\
        """, nativeQuery = true)
        public List<InvoiceDto> findAllInvoices();

    @Query(value = """
        SELECT\s
        i.id AS "id",\s
        i.date AS "date",\s
        i.id AS "invoiceNumber",\s
        ROUND(SUM(invoice_line.qty * item_price.price), 2) AS "total"\s
        FROM invoice i\s
        INNER JOIN invoice_line ON invoice_line.invoice_id = i.id\s
        INNER JOIN item_price ON invoice_line.item_id = item_price.item_id\s
        INNER JOIN customer ON i.customer_id = customer.id\s
        WHERE i.customer_id = :customerId\s
        GROUP BY i.\s
        ORDER BY "date"\
        """, nativeQuery = true)
        public List<InvoiceDto> findAllInvoiceByCustomerId(@Param("customerId") Long customerId);

        @Query(value = """
        SELECT\s
        i.id AS "id",\s
        i.date AS "date",\s
        i.id AS "invoiceNumber",\s
        ROUND(SUM(invoice_line.qty * item_price.price), 2) AS "total"\s
        FROM invoice i\s
        INNER JOIN invoice_line ON invoice_line.invoice_id = i.id\s
        INNER JOIN item_price ON invoice_line.item_id = item_price.item_id\s
        WHERE so.date LIKE CONCAT(:year, '-', :month, '-', '0', '%')\s
        GROUP BY i.\s
        ORDER BY "date"\
        """, nativeQuery = true)
        public List<InvoiceDto> findByYearAndMonth(@Param("year") int year,
        @Param("month") String month);

        @Query(value = """
             SELECT\s
            FORMATDATETIME(i.date, 'MMM') AS "Month",\s
            ROUND(SUM(invoice_line.qty * item_price.price), 2) AS "Total"\s
            FROM invoice i\s
            INNER JOIN invoice_line ON invoice_line.invoice_id = i.id\s
            INNER JOIN item_price ON invoice_line.item_id = item_price.item_id\s
            WHERE EXTRACT(MONTH FROM Date) >= EXTRACT(MONTH FROM CURRENT_DATE()) - 3\s
            AND EXTRACT(MONTH FROM Date) < EXTRACT(MONTH FROM CURRENT_DATE())\s
            GROUP BY FORMATDATETIME(i.date, 'MMM')\s
            ORDER BY FORMATDATETIME(i.date, 'MMM') ASC\
            """, nativeQuery = true)
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
