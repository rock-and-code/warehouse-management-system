package com.example.warehouseManagement.Domains;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "picking_job")
public class PickingJob {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Builder.Default
    @Column(name = "date")
    private LocalDate date = LocalDate.now();
    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;
    @OneToMany(mappedBy = "pickingJob", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "picking_job_lines")
    @Builder.Default
    private List<PickingJobLine> pickingJobLines = new ArrayList<>();
    @Builder.Default
    private PjStatus status = PjStatus.PENDING;

    public enum PjStatus {
        PENDING, //0
        FULFILLED //1
    }
}
