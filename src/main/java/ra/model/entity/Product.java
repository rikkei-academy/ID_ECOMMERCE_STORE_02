package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductId")
    private int productId;
    @Column(name = "ProductName", columnDefinition = "nvarchar(50)", nullable = false, unique = true)
    private String productName;
    @Column(name = "ProductStatus")
    private boolean productStatus;
    @Column(name = "Discription", columnDefinition = "nvarchar(225)")
    private String discription;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "Price", columnDefinition = "nvarchar(225)")
    private String price;
    @Column(name = "BuyDate")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date buyDate;
    @Column(name = "CreateDate")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createDate;
    @Column(name = "Discount")
    private float discount;
    @Column(name = "Sold")
    private int sold;
    @Column(name= "Image",  columnDefinition = "text")
    private String  image;

    @ManyToOne
    @JoinColumn( name= "catalogId")
    @JsonIgnore
    private  Catalog catalog;
//    @OneToMany(mappedBy = "product")
//    private List<OrderDetail> listOrderDetail = new ArrayList<>();

}
