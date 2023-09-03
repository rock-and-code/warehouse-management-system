package com.example.warehouseManagement.Domains;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "street")
    private String street;
    @Column(name = "city")
    private String city;
    @Column(name = "state")
    private String state;
    @Column(name = "zipcode")
    private int zipcode;
    @Column(name = "contact_info")
    private String contactInfo;
    //Specify the field in the sales order table to map the relationship with
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY) //Fetch when needed
    @Column(name = "sales_orders")
    @Builder.Default
    List<SalesOrder> salesOrders = new ArrayList<>();

    @Column(name = "formatted_address")
    public String formattedAddress() {
        return String.format("%s %s, %s, %d", street, city, state, zipcode);
    }
}

//MANY TO MANY RELATIONSHIP ANNOTATION
/**
 * @ManyToMany
    @JoinTable(name="CUSTOMER_SALEORDER", joinColumns = @JoinColumn(name="CUSTOMER_ID"), 
        inverseJoinColumns = @JoinColumn(name="SALEORDER_ID"))
 */
