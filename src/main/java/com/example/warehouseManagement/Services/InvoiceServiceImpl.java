package com.example.warehouseManagement.Services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.warehouseManagement.Domains.Invoice;
import com.example.warehouseManagement.Domains.InvoiceLine;
import com.example.warehouseManagement.Domains.PickingJob;
import com.example.warehouseManagement.Domains.PickingJobLine;
import com.example.warehouseManagement.Repositories.InvoiceLineRepository;
import com.example.warehouseManagement.Repositories.InvoiceRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceLineRepository invoiceLineRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
    }

    @Override
    public Iterable<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice createByPickingJob(PickingJob pickingJob) {
        Invoice invoice = Invoice.builder().customer(pickingJob.getSalesOrder().getCustomer()).salesOrder(pickingJob.getSalesOrder()).build();
        Invoice savedInvoice = invoiceRepository.save(invoice);
        for (int i=0; i<pickingJob.getPickingJobLines().size(); i++) {
            PickingJobLine pickingJobLine = pickingJob.getPickingJobLines().get(i);
            System.out.printf("PickingJobLine qtyPicked: %d\n", pickingJobLine.getQtyPicked());
            if (pickingJobLine.getQtyPicked() > 0) {
                InvoiceLine invoiceLine = InvoiceLine.builder().invoice(savedInvoice).item(pickingJobLine.getItem()).qty(pickingJobLine.getQtyPicked()).build();
                InvoiceLine savedInvoiceLine = invoiceLineRepository.save(invoiceLine);
                savedInvoice.getInvoiceLines().add(savedInvoiceLine);
            }
        } 
        return invoiceRepository.save(savedInvoice);
    }

    @Override
    public void delete(Invoice invoice) {
        invoiceRepository.delete(invoice);
    }

}
