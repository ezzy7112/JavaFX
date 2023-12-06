package Controller;

import Model.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class appController implements Initializable {

    @FXML
    static ArrayList<productClass> productRecord = new ArrayList();
    @FXML
    private static appModel model;
    @FXML
    ArrayList<stockClass> stockRecord = new ArrayList();
    int productID;
    String proName;
    int proQuantity;
    int proPrice;
    //---------Staff Tab----------------------------------------------------------------------------------
    int staffID;
    String stfBranch, stfFirstName, stfPassword, stfAddress, stfEmail, stfContactNumber;
    int stfCreatedAt;

    //---------Customer Tab----------------------------------------------------------------------------------
    @FXML
    int customerID;
    @FXML
    String cusName, cusAddress, cusEmail, cusContactNumber, cusCreatedAt;
    private dbConnection dbConnection;

    //---------QR Code----------------------------------------------------------------------------------
    @FXML
    private Button startBTN, rtnStartBTN;
    @FXML
    private Label code;
    @FXML
    private Label fps;
    @FXML
    private ImageView imageView, rtnImageView;
    //---------Stock Tab----------------------------------------------------------------------------------
    @FXML
    private TextField stkProNameTf;
    @FXML
    private TextField stkProPriceTf;
    @FXML
    private TextField stkSearchTf;
    @FXML
    private TextField stkProDIdTf;
    @FXML
    private TextField stkProDNameTf;
    @FXML
    private TextField stkProDLUpdateTf;
    @FXML
    private TextField stkProDDiscountTf;
    @FXML
    private TextArea stkProDescriptionTa, stkProDDescriptionTa;
    @FXML
    private Button stkSearchBTN;
    @FXML
    private ImageView stkQRIV;
    @FXML
    private Button stkAddCode;

    //---------Query Tab--------------------------------------------------------------------------------------------
    @FXML
    private Button qCreateBTN;
    @FXML
    private DatePicker qFromDateDP;
    @FXML
    private DatePicker qToDateDP;
    @FXML
    private TableView<receiptClass> qResultsTV;
    @FXML
    private ObservableList<receiptClass> qResultsOL;
    @FXML
    private ObservableList<String> qSearchOL = FXCollections.observableArrayList("Most Popular", "Least Popular");
    @FXML
    private TableColumn<receiptClass, String> qResultsNameTC;
    @FXML
    private TableColumn<receiptClass, Integer> qResultsProIDTC, qResultsQuantityTC;
    @FXML
    private TableColumn<receiptClass, Boolean> qResultsPPriceTC;
    @FXML
    private TextField qResultsTPriceTF;
    @FXML
    private ComboBox<String> qSearchCMB;
    @FXML
    private CategoryAxis queryX;
    @FXML
    private NumberAxis queryY;
    @FXML
    private BarChart<?,?> queryChart;

    @FXML
    private CategoryAxis recordX;
    @FXML
    private NumberAxis recordY;
    @FXML
    private LineChart<?,?> recordChart;

    private int rcpProductID, rcpProQuantity;
    private String rcpProName;
    private Boolean rcpProPrice;


    //---------??? Tab----------------------------------------------------------------------------------
    @FXML
    private TextField LDstfIdTf, LDstfNameTf, LDstfRevenueTf;
    @FXML
    private Button stkdeleteProDbtn;


    // variables for adding staff.//////////////////////////////////////////////////////////////////////

    private ObservableList<String> stfRole = FXCollections.observableArrayList("Staff", "Manager");
    @FXML
    private ComboBox<String> nsStaffDRoleCMB;
    @FXML
    private TextField nsStaffDFNameTf, nsStaffDLNameTf, nsStaffDAddressTf, nsStaffDEmailTf, nsStaffDPasswordTf, nsStaffDConfirmPTf, nsStaffDContactNTf;


    // variables for adding customers.//////////////////////////////////////////////////////////////////////
    @FXML
    private TextField acCusDNameTf, acCusDAddressTf, acCusDEmailTf, acCusDContactNTf;
    @FXML
    private Button acDeleteBTN;

    // variables for staff.//////////////////////////////////////////////////////////////////////
    @FXML
    private TextField stfDNameTf, stfDLNameTf, stfDEmailTf, stfDContactTf;
    @FXML
    private TextArea stfDAddressTf;

    // variables for Sale tab.//////////////////////////////////////////////////////////////////////
    @FXML
    private TextField sProductIDTF, sCustomerTF, sCashTF, sTotalPriceTF, sAmountTF, sProductNameTF, ProPriceTF;
    @FXML
    private TextArea sDescriptionTF;
    @FXML
    private Button sAddBTN, s1BTN, s2BTN, s3BTN, s4BTN, s5BTN, s6BTN, s7BTN, s8BTN, s9BTN, s0BTN, sPBTN, sClearBTN, sProceedBTN, saleClearBTN;

    private double totalSPrice = 0;

    // variables for return tab.//////////////////////////////////////////////////////////////////////
    @FXML
    private ObservableList<receiptClass> returnBasket;
    @FXML
    private TextField rtnReceiptIDTF, rtnCustomerIDTF, rtnStaffIDTF, rtnTotalPTF, rtnDateTF, rtnPasswordTF;
    @FXML
    private Button returnClearBTN, r1BTN, r2BTN, r3BTN, r4BTN, r5BTN, r6BTN, r7BTN, r8BTN, r9BTN, r0BTN, rPBTN, rProceedBTN, rClearBTN;
    @FXML
    private TableView<receiptClass> tvReturnDetails;
    @FXML
    private TableColumn<receiptClass, String> rtnProNameClm, rtnProQuantityClm;
    @FXML
    private TableColumn<receiptClass, Integer> rtnProductIDClm;
    @FXML
    private TableColumn<receiptClass, Double> rtnProPriceClm;

    private double totalRPrice = 0;

    //the variables below is for the table view....................
    @FXML
    private TableView<stockClass> stkProTable;
    @FXML
    private TableColumn<stockClass, String> stkProductNColumn;
    @FXML
    private TableColumn<stockClass, Integer> stkProductQColumn, stkProductPColumn, stkProIDColumn;
    @FXML
    private ObservableList<stockClass> stkProducts = FXCollections.observableArrayList();

    //-------staff---------------------------------------------------------------------
    private int stfId;
    private double revenue;
    private String stfFName;

    //-------Notification---------------------------------------------------------------------------
    @FXML
    private TextField ntfTimeTF;

    //the variables below is for the staff table view....................
    @FXML
    private ObservableList<staffClass> staffOL;
    @FXML
    private TableView<staffClass> stfDTable;
    @FXML
    private TableColumn<staffClass, Integer> stfStaffIDColumn, stfCreatedColumn;
    @FXML
    private TableColumn<staffClass, String> stfRoleColumn, stfNameColumn, stfPasswordColumn, stfAddressColumn, stfEmailColumn, stfCNumberColumn;
    @FXML
    private Button asDeleteBTN;

    //this lines below is for the customer table in the tab.---------------------------------------------

    @FXML
    private ObservableList<customerClass> customerOL;
    @FXML
    private TableView<customerClass> customerDTable;
    @FXML
    private TableColumn<customerClass, Integer> cusTIDColumn;
    @FXML
    private TableColumn<customerClass, String> cusTNameColumn, cusTAddressColumn, cusTEmailColumn, cusTCNumberColumn, cusTCreatedColumn;

    //this lines below is for the customer tab in the tab.---------------------------------------------
    @FXML
    private TextField cusDNameTf, cusDEmailTf, cusDContactNTf;
    @FXML
    private TextArea cusDAddressTf;

    //---------------Sale---------------------------------------------------------------
    @FXML
    private ObservableList<receiptClass> customerBasket;
    @FXML
    private ObservableList<receiptClass> productQRCodes;
    @FXML
    private TableView<receiptClass> tvOrderDetails;
    @FXML
    private TableColumn<receiptClass, Integer> tcOrderID;
    @FXML
    private TableColumn<receiptClass, String> tcOrderProName, tcOrderPrice, tcOrderQuantity;

    //-----------------buttons////////////////////////////////////////////
    @FXML
    private Button stockMBTN, saleMBTN, staffMBTN, queryMBTN, settingMBTN, customerMBTN, asNewSBTN, asExitBTN, newCusBTN, acCusDExitBTN, returnMBTN;

    //-------------------tab ------------------////////////////////////////////////////////
    @FXML
    private TabPane centreTabP;
    @FXML
    private Tab stockTab, saleTab, settingTab, queryTab, staffTab, addSTab, customerTab, addCTab, returnTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        model = new appModel();
        dbConnection = new dbConnection();
        nsStaffDRoleCMB.setItems(stfRole);
        customerBasket = FXCollections.observableArrayList();
        productQRCodes = FXCollections.observableArrayList();
        returnBasket = FXCollections.observableArrayList();
        qResultsOL = FXCollections.observableArrayList();
        setStockProductInfo();
        setStaffInfo();
        setCustomerInfo();
        hideTabs();
        searchStockProductInfo();

        qSearchCMB.setItems(qSearchOL);

        saleButtons();
        returnButtons();

        totalSPrice = 0;

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);
            int minute = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR);

            String tZone = "";

            if (hour >= 12) {

                tZone = " PM";
            } else {
                tZone = " AM";
            }

            ntfTimeTF.setText(hour + ":" + (minute) + ":" + second + tZone);
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();


        startBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (sProductIDTF.getText().equals("")) {

                    WebCamImageProvider imageProvider = new WebCamImageProvider();

                    imageView.imageProperty().bind(imageProvider.imageProperty());
                    fps.textProperty().bind(imageProvider.fpsProperty());

                    imageProvider.setOnSucceeded(event1 -> code.setText(imageProvider.getQrCode()));
                    imageProvider.setOnSucceeded(event1 -> setQRCode(imageProvider.getQrCode()));
                    System.out.println("qr code is : " + imageProvider.imageProperty().toString());


                    Thread t = new Thread(imageProvider);
                    t.setDaemon(true);
                    t.start();
                    System.out.println("scanning!!");
                } else {

                    getSearchDetails();

                }
            }
        });

        rtnStartBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (rtnReceiptIDTF.getText().equals("")) {

                    WebCamImageProvider imageProvider = new WebCamImageProvider();
                    try {
                        //imageProvider.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    rtnImageView.imageProperty().bind(imageProvider.imageProperty());

                    imageProvider.setOnSucceeded(event1 -> setRQRCode(imageProvider.getQrCode()));

                    System.out.println("qr code is : " + imageProvider.imageProperty().toString());

                    Thread t = new Thread(imageProvider);
                    t.setDaemon(true);
                    t.start();
                    System.out.println("scanning!!");
                } else {

                    getSearchRDetails();

                }
            }
        });

        stkAddCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                WebCamImageProvider imageProvider = new WebCamImageProvider();

                stkQRIV.imageProperty().bind(imageProvider.imageProperty());

                System.out.println("qr code is : " + imageProvider.imageProperty().toString());
                imageProvider.setOnSucceeded(event1 -> {
                    try {
                        setStockProductQR(imageProvider.getQrCode());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                Thread t = new Thread(imageProvider);
                t.setDaemon(true);
                t.start();
                System.out.println("scanning!!");


            }
        });


    }

    public void setQRCode(String qrCodeString) {
        sProductIDTF.setText(qrCodeString);
        getSearchDetails();

    }

    public void setRQRCode(String qrCodeString) {
        rtnReceiptIDTF.setText(qrCodeString);
        getSearchRDetails();

    }

    public void getProDetails(ActionEvent actionEvent) {

        try {
            System.out.println("here");

            String proName = stkProNameTf.getText();
            String proDescription = stkProDescriptionTa.getText();
            int proPrice = Integer.parseInt(stkProPriceTf.getText());

            ResultSet rs;
            PreparedStatement pst = null;

            Connection connection = Model.dbConnection.connectDatabase();

            String insertSql = "INSERT INTO products(staffID, proName, proDescription, proPrice, current_timestamp)VALUES (?,?,?,?)";
            pst = connection.prepareStatement(insertSql);
            pst.setInt(1, stfId);
            pst.setString(2, proName);
            pst.setString(3, proDescription);
            pst.setInt(4, proPrice);

            pst.executeUpdate();

            System.out.println("record added!");

            loadData();


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void staffLoginDetails(int stfId, String stfFName, double stfRevenue) {


        //LDstfIdTf.setText(toString(stfId));
        LDstfNameTf.setText(stfFName);
        LDstfRevenueTf.setText(String.valueOf(stfRevenue));

        this.stfId = stfId;
        this.stfFName = stfFName;
        this.revenue = stfRevenue;
    }

    public void setStockProductInfo() {

        stkProTable.setOnMouseClicked(event -> {
            stkdeleteProDbtn.setVisible(true);
            stockClass proTableList = stkProTable.getItems().get(stkProTable.getSelectionModel().getSelectedIndex());
            stkProDIdTf.setText(Integer.toString(proTableList.getProductID()));
            stkProDNameTf.setText(proTableList.getProName());
            stkProDDescriptionTa.setText(proTableList.getProDescription());
            stkProDLUpdateTf.setText(Integer.toString(proTableList.getProductID()));
            stkProDDiscountTf.setText(Integer.toString(proTableList.getProductID()));

        });

    }

    public void setStockProductQR(String qrCode) throws SQLException {

        try {
            int proQan = 0;
            int proID = Integer.parseInt(stkProDIdTf.getText());

            PreparedStatement pst = null;


            Connection connection = Model.dbConnection.connectDatabase();


            String sql = "INSERT INTO product_codes(productID, proBarcode, proStatus)VALUES (?,?,?)";

            pst = connection.prepareStatement(sql);

            pst.setInt(1, proID);
            pst.setString(2, qrCode);
            pst.setString(3, "ACTIVE");

            pst.executeUpdate();
            pst.close();

            System.out.println("record added!");

            String sqlQa = "SELECT * FROM products WHERE productID ='" + stkProDIdTf.getText() + "'";
            ResultSet rs;

            rs = connection.createStatement().executeQuery(sqlQa);

            if (rs.next()) {

                proQan = rs.getInt("proQuantity");
            }

            rs.close();

            System.out.println("qaunt is: " + proQan);

            proQan = proQan + 1;

            System.out.println("qaunt after is: " + proQan);

            String insertSql = "UPDATE `products` SET `proQuantity` = '" + proQan + "'" + "WHERE `productID` = '" + stkProDIdTf.getText() + "'";

            pst = connection.prepareStatement(insertSql);
            pst.executeUpdate();
            pst.close();
            loadData();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("QR Added!");
            alert.showAndWait();


        } catch (Exception e) {

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("QR  Exist!");
            alert.showAndWait();
            e.printStackTrace();
        }

    }

    public void searchStockProductInfo() {

        //stockClass proTableList = stkProTable.getItems().get(stkProTable.getSelectionModel().getSelectedIndex());


        FilteredList<stockClass> stkFilteredList = new FilteredList<>(stkProducts, e -> true);
        stkSearchTf.setOnKeyReleased(event1 -> {
            stkSearchTf.textProperty().addListener((observableValue, oldValue, newValue) -> {

                stkFilteredList.setPredicate((Predicate<? super stockClass>) stockclass -> {

                    if (newValue == null || newValue.isEmpty()) {

                        return true;

                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (stockclass.getSProductID().contains(newValue)) {

                        return true;

                    } else if (stockclass.getProName().toLowerCase().contains(lowerCaseFilter)) {

                        return true;
                    }
                    return false;
                });

            });

            SortedList<stockClass> sortedData = new SortedList<>(stkFilteredList);
            sortedData.comparatorProperty().bind(stkProTable.comparatorProperty());
            stkProTable.setItems(sortedData);
        });

           /* stockClass proTableList = stkProTable.getItems().get(stkProTable.getSelectionModel());
            stkProDIdTf.setText(Integer.toString(proTableList.getProductID()));
            stkProDNameTf.setText(proTableList.getProName());
            stkProDDescriptionTa.setText(proTableList.getProDescription());
            stkProDLUpdateTf.setText(Integer.toString(proTableList.getProductID()));
            stkProDDiscountTf.setText(Integer.toString(proTableList.getProductID()));

            stkSearchTf.getText();*/


    }

    public void setStaffInfo() {

        stfDTable.setOnMouseClicked(event -> {
            asDeleteBTN.setVisible(true);
            staffClass stfTableList = stfDTable.getItems().get(stfDTable.getSelectionModel().getSelectedIndex());
            stfDNameTf.setText(stfTableList.getStfFirstName());
            stfDLNameTf.setText(stfTableList.getStfLastName());
            stfDAddressTf.setText(stfTableList.getStfAddress());
            stfDEmailTf.setText(stfTableList.getStfEmail());
            stfDContactTf.setText(stfTableList.getStfContactNumber());

        });
    }

    public void setCustomerInfo() {

        customerDTable.setOnMouseClicked(event -> {
            acDeleteBTN.setVisible(true);
            customerClass cusTableList = customerDTable.getItems().get(customerDTable.getSelectionModel().getSelectedIndex());
            cusDNameTf.setText(cusTableList.getCusName());
            cusDAddressTf.setText(cusTableList.getCusAddress());
            cusDEmailTf.setText(cusTableList.getCusEmail());
            cusDContactNTf.setText(cusTableList.getCusContactNumber());

        });
    }

    public void deleteProD(ActionEvent actionEvent) throws SQLException {

        try {

            Connection connection = Model.dbConnection.connectDatabase();

            String deleteSql = "DELETE FROM products WHERE productID = '" + stkProDIdTf.getText() + "'";

            PreparedStatement pst = connection.prepareStatement(deleteSql);

            pst.executeUpdate();
            pst.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("Content Deleted!");
            alert.showAndWait();

            stkdeleteProDbtn.setVisible(false);

            loadData();

        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    public void deleteStaffD(ActionEvent actionEvent) throws SQLException {
        try {
            int staffID;

            asDeleteBTN.setVisible(true);
            staffClass stfTableList = stfDTable.getItems().get(stfDTable.getSelectionModel().getSelectedIndex());
            staffID = stfTableList.getStaffID();


            Connection connection = Model.dbConnection.connectDatabase();

            String deleteSql = "DELETE FROM staff WHERE staffID = '" + staffID + "'";

            PreparedStatement pst = connection.prepareStatement(deleteSql);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("Staff Deleted!");
            alert.showAndWait();

            pst.executeUpdate();
            pst.close();

            asDeleteBTN.setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();

        }

        loadDataStaff();

    }

    public void deleteCustomerD(ActionEvent actionEvent) throws SQLException {
        try {
            int customerID;

            acDeleteBTN.setVisible(true);
            customerClass proTableList = customerDTable.getItems().get(customerDTable.getSelectionModel().getSelectedIndex());
            customerID = proTableList.getCustomerID();


            Connection connection = Model.dbConnection.connectDatabase();

            String deleteSql = "DELETE FROM customer WHERE customerID = '" + customerID + "'";

            PreparedStatement pst = connection.prepareStatement(deleteSql);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("Customer Deleted!");
            alert.showAndWait();

            pst.executeUpdate();
            pst.close();

            acDeleteBTN.setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();

        }

        loadDataCustomer();

    }

    public void saveProductInfo(ActionEvent actionEvent) throws SQLException {

        try {
            System.out.println("Save");

            String proName = stkProDNameTf.getText();
            String proDescription = stkProDescriptionTa.getText();
            int ProDLUpdate = Integer.parseInt(stkProDLUpdateTf.getText());
            int ProDDiscount = Integer.parseInt(stkProDDiscountTf.getText());


            PreparedStatement pst = null;


            Connection connection = Model.dbConnection.connectDatabase();

            int stkUpdatedAt = 8;
            String proBarcode = "";
            String proStatus = "";


            String insertSql = "UPDATE products SET staffID = ?, proName = ?, proDescription = ?, ProDiscount = ?, stkUpdatedAt = ? WHERE productID = '" + stkProDIdTf.getText() + "'";

            pst = connection.prepareStatement(insertSql);
            pst.setInt(1, stfId);
            pst.setString(2, proName);
            pst.setString(3, proDescription);
            pst.setInt(4, ProDDiscount);
            pst.setInt(5, ProDLUpdate);

            pst.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("Content Updated!");
            alert.showAndWait();


            pst.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        loadData();

    }


    public void updateCustomerInfo(ActionEvent actionEvent) throws SQLException {

        try {
            System.out.println("Save");
            customerClass cusTableList = customerDTable.getItems().get(customerDTable.getSelectionModel().getSelectedIndex());

            int cusDID = cusTableList.getCustomerID();
            String cusDName = cusDNameTf.getText();
            String cusDEmail = cusDEmailTf.getText();
            String cusDAddress = cusDAddressTf.getText();
            String cusDContactN = cusDContactNTf.getText();


            PreparedStatement pst = null;


            Connection connection = Model.dbConnection.connectDatabase();

            int stkUpdatedAt = 8;
            String proBarcode = "";
            String proStatus = "";


            String insertSql = "UPDATE customer SET cusAddress = ?, cusEmail = ?, cusContactNumber = ? WHERE customerID = '" + cusDID + "'";

            pst = connection.prepareStatement(insertSql);
            pst.setString(1, cusDAddress);
            pst.setString(2, cusDEmail);
            pst.setString(3, cusDContactN);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("Content Updated!");
            alert.showAndWait();

            pst.executeUpdate();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        loadDataCustomer();

    }

    public void updateStaffInfo(ActionEvent actionEvent) throws SQLException {

        try {

            System.out.println("Save");

            staffClass stfTableList = stfDTable.getItems().get(stfDTable.getSelectionModel().getSelectedIndex());

            int stfDID = stfTableList.getStaffID();
            String stfDName = stfDNameTf.getText();
            String stfDLName = stfDLNameTf.getText();
            String stfDAddress = stfDAddressTf.getText();
            String stfDEmail = stfDEmailTf.getText();
            String stfDContact = stfDContactTf.getText();

            PreparedStatement pst = null;


            Connection connection = Model.dbConnection.connectDatabase();


            String insertSql = "UPDATE staff SET stfFName = ?, stfLName = ?, stfAddress = ?, stfEmail = ?, stfContactNumber = ? WHERE staffID = '" + stfDID + "'";

            pst = connection.prepareStatement(insertSql);
            pst.setString(1, stfDName);
            pst.setString(2, stfDLName);
            pst.setString(3, stfDAddress);
            pst.setString(4, stfDEmail);
            pst.setString(5, stfDContact);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("Content Updated!");
            alert.showAndWait();

            pst.executeUpdate();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        loadDataStaff();

    }

    public void loadData() {

        stkProducts.clear();
        productRecord.clear();

        ResultSet rs;
        ResultSet rs2;
        String sql = "SELECT * FROM products";


        Connection connection = Model.dbConnection.connectDatabase();

        try {

            rs = connection.createStatement().executeQuery(sql);

            while (rs.next()) {

                String sql2 = "SELECT * FROM product_codes WHERE productID ='" + rs.getInt("productID") + "'";

                System.out.printf("heelo " + rs.getString(4));

                rs2 = connection.createStatement().executeQuery(sql2);

                while (rs2.next()) {

                    System.out.printf("and " + rs2.getString(2));

                    productClass product1 = new productClass(rs2.getInt("productID"), rs2.getInt("receiptID"), rs2.getString("proBarcode"), rs2.getString("proStatus"));

                    productRecord.add(product1);

                }

                stkProducts.add(new stockClass(rs.getInt("productID"), rs.getInt("staffID"), rs.getString("proName"), rs.getString("proDescription"), rs.getDouble("proPrice"), rs.getInt("proQuantity"), rs.getInt("stkUpdatedAt"), productRecord));
                rs2.close();

            }

            rs.close();

            stkProIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
            stkProIDColumn.setMinWidth(50);

            stkProductNColumn.setCellValueFactory(new PropertyValueFactory<>("proName"));
            stkProductNColumn.setMinWidth(100);

            stkProductQColumn.setCellValueFactory(new PropertyValueFactory<>("proQuantity"));
            stkProductQColumn.setMinWidth(100);

            stkProductPColumn.setCellValueFactory(new PropertyValueFactory<>("proPrice"));
            stkProductPColumn.setMinWidth(75);


            stkProTable.setItems(null);
            stkProTable.setMinHeight(200);
            stkProTable.setItems(stkProducts);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadDataStaff() {

        ResultSet rsStf;
        String sql = "SELECT * FROM staff";

        Connection connection = Model.dbConnection.connectDatabase();
        staffOL = FXCollections.observableArrayList();

        try {

            rsStf = connection.createStatement().executeQuery(sql);


            while (rsStf.next()) {

                System.out.println("n/ stafffffffffffffffff" + rsStf.getString(3));

                staffOL.add(new staffClass(rsStf.getInt(1), rsStf.getString(2), rsStf.getString(3), rsStf.getString(4), rsStf.getString(5), rsStf.getString(6), rsStf.getString(7), rsStf.getString(8), rsStf.getInt(9), rsStf.getDouble(10)));

            }

            rsStf.close();

            stfStaffIDColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));
            stfStaffIDColumn.setMinWidth(70);

            stfRoleColumn.setCellValueFactory(new PropertyValueFactory<>("staffRole"));
            stfRoleColumn.setMinWidth(70);

            stfNameColumn.setCellValueFactory(new PropertyValueFactory<>("stfFirstName"));
            stfNameColumn.setMinWidth(70);

            stfPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("stfPassword"));
            stfPasswordColumn.setMinWidth(70);

            stfAddressColumn.setCellValueFactory(new PropertyValueFactory<>("stfAddress"));
            stfAddressColumn.setMinWidth(70);

            stfEmailColumn.setCellValueFactory(new PropertyValueFactory<>("stfEmail"));
            stfEmailColumn.setMinWidth(70);

            stfCNumberColumn.setCellValueFactory(new PropertyValueFactory<>("stfContactNumber"));
            stfCNumberColumn.setMinWidth(70);

            stfCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("stfCreatedAt"));
            stfCreatedColumn.setMinWidth(70);

            stfDTable.setItems(null);
            stfDTable.setMinHeight(200);
            stfDTable.setItems(staffOL);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadDataCustomer() {

        ResultSet rsCus;
        String sql = "SELECT * FROM customer";

        Connection connection = Model.dbConnection.connectDatabase();
        customerOL = FXCollections.observableArrayList();

        try {

            rsCus = connection.createStatement().executeQuery(sql);


            while (rsCus.next()) {

                System.out.println("n/ stafffffffffffffffff" + rsCus.getString(3));

                customerOL.add(new customerClass(rsCus.getInt(1), rsCus.getString(2), rsCus.getString(3), rsCus.getString(4), rsCus.getString(5), rsCus.getString(6)));

            }

            rsCus.close();

            cusTIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            cusTIDColumn.setMinWidth(70);

            cusTNameColumn.setCellValueFactory(new PropertyValueFactory<>("cusName"));
            cusTNameColumn.setMinWidth(70);

            cusTAddressColumn.setCellValueFactory(new PropertyValueFactory<>("cusAddress"));
            cusTAddressColumn.setMinWidth(70);

            cusTEmailColumn.setCellValueFactory(new PropertyValueFactory<>("cusEmail"));
            cusTEmailColumn.setMinWidth(70);

            cusTCNumberColumn.setCellValueFactory(new PropertyValueFactory<>("cusContactNumber"));
            cusTCNumberColumn.setMinWidth(70);

            cusTCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("cusCreatedAt"));
            cusTCreatedColumn.setMinWidth(70);


            customerDTable.setItems(null);
            customerDTable.setMinHeight(200);
            customerDTable.setItems(customerOL);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveCustomerInfo(ActionEvent actionEvent) throws SQLException {

        try {
            System.out.println("Save");

            String cusName = acCusDNameTf.getText();
            String cusAddress = acCusDAddressTf.getText();
            String cusEmail = acCusDEmailTf.getText();
            String cusContactN = acCusDContactNTf.getText();
            String cusCreatedOn = "g";

            PreparedStatement pst = null;

            Connection connection = Model.dbConnection.connectDatabase();

            String insertSql = "INSERT INTO customer(cusName, cusAddress, cusEmail, cusContactNumber, cusCreatedOn) VALUES(?,?,?,?,?)";

            pst = connection.prepareStatement(insertSql);

            pst.setString(1, cusName);
            pst.setString(2, cusAddress);
            pst.setString(3, cusEmail);
            pst.setString(4, cusContactN);
            pst.setString(5, cusCreatedOn);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("Customer Added!");
            alert.showAndWait();

            pst.executeUpdate();
            pst.close();

            loadDataCustomer();

        } catch (Exception e) {
            e.printStackTrace();

        }

        loadData();

    }

    public void saveStaffInfo(ActionEvent actionEvent) throws SQLException {

        try {
            System.out.println("Save");

            String stfRoleV = nsStaffDRoleCMB.getValue();
            String stfFName = nsStaffDFNameTf.getText();
            String stfLName = nsStaffDLNameTf.getText();
            String stfAddress = nsStaffDAddressTf.getText();
            String stfEmail = nsStaffDEmailTf.getText();
            String stfPassword = nsStaffDPasswordTf.getText();
            String stfConfirmP = nsStaffDConfirmPTf.getText();
            String stfContactN = nsStaffDContactNTf.getText();
            int stfCreatedOn = 0;
            double stfRevenue = 2;

            PreparedStatement pst = null;

            Connection connection = Model.dbConnection.connectDatabase();

            String insertSql = "INSERT INTO staff(staffRole, stfFName, stfLName, stfAddress, stfEmail, stfPassword, stfContactNumber, stfCreatedAt, stfRevenue) VALUES(?,?,?,?,?,?,?,?,?)";

            pst = connection.prepareStatement(insertSql);

            pst.setString(1, stfRoleV);
            pst.setString(2, stfFName);
            pst.setString(3, stfLName);
            pst.setString(4, stfAddress);
            pst.setString(5, stfEmail);
            pst.setString(6, stfPassword);
            pst.setString(7, stfContactN);
            pst.setInt(8, stfCreatedOn);
            pst.setDouble(9, stfRevenue);


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Information");
            alert.setHeaderText(null);
            alert.setContentText("Staff Added!");
            alert.showAndWait();

            pst.executeUpdate();
            pst.close();

            loadDataStaff();

        } catch (Exception e) {
            e.printStackTrace();

        }

        loadData();

    }

    public void hideTabs() {


        centreTabP.getTabs().remove(stockTab);
        centreTabP.getTabs().remove(settingTab);
        centreTabP.getTabs().remove(queryTab);
        centreTabP.getTabs().remove(staffTab);
        centreTabP.getTabs().remove(addSTab);
        centreTabP.getTabs().remove(addCTab);
        centreTabP.getTabs().remove(customerTab);
        centreTabP.getTabs().remove(returnTab);

        saleMBTN.getStyleClass().add("MBTNClick");
        saleMBTN.setDisable(true);

        stockMBTN.setOnMouseClicked(event -> {
            System.out.printf("StockClick");
            stockMBTN.getStyleClass().add("MBTNClick");
            returnMBTN.getStyleClass().remove("MBTNClick");
            saleMBTN.getStyleClass().remove("MBTNClick");
            settingMBTN.getStyleClass().remove("MBTNClick");
            queryMBTN.getStyleClass().remove("MBTNClick");
            staffMBTN.getStyleClass().remove("MBTNClick");
            customerMBTN.getStyleClass().remove("MBTNClick");

            stockMBTN.setDisable(true);
            returnMBTN.setDisable(false);
            saleMBTN.setDisable(false);
            settingMBTN.setDisable(false);
            queryMBTN.setDisable(false);
            staffMBTN.setDisable(false);
            customerMBTN.setDisable(false);

            centreTabP.getTabs().add(stockTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().remove(customerTab);

        });

        returnMBTN.setOnMouseClicked(event -> {
            System.out.printf("returnClick");

            stockMBTN.getStyleClass().remove("MBTNClick");
            saleMBTN.getStyleClass().remove("MBTNClick");
            settingMBTN.getStyleClass().remove("MBTNClick");
            queryMBTN.getStyleClass().remove("MBTNClick");
            staffMBTN.getStyleClass().remove("MBTNClick");
            customerMBTN.getStyleClass().remove("MBTNClick");
            returnMBTN.getStyleClass().add("MBTNClick");

            returnMBTN.setDisable(true);
            stockMBTN.setDisable(false);
            saleMBTN.setDisable(false);
            settingMBTN.setDisable(false);
            queryMBTN.setDisable(false);
            staffMBTN.setDisable(false);
            customerMBTN.setDisable(false);

            centreTabP.getTabs().add(returnTab);
            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().remove(customerTab);

        });

        saleMBTN.setOnMouseClicked(event -> {
            System.out.printf("SaleClick");
            saleMBTN.getStyleClass().add("MBTNClick");
            returnMBTN.getStyleClass().remove("MBTNClick");
            stockMBTN.getStyleClass().remove("MBTNClick");
            settingMBTN.getStyleClass().remove("MBTNClick");
            queryMBTN.getStyleClass().remove("MBTNClick");
            staffMBTN.getStyleClass().remove("MBTNClick");
            customerMBTN.getStyleClass().remove("MBTNClick");

            stockMBTN.setDisable(false);
            returnMBTN.setDisable(false);
            saleMBTN.setDisable(true);
            settingMBTN.setDisable(false);
            queryMBTN.setDisable(false);
            staffMBTN.setDisable(false);
            customerMBTN.setDisable(false);

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().add(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().remove(customerTab);
        });

        settingMBTN.setOnMouseClicked(event -> {
            System.out.printf("SettingClick");
            settingMBTN.getStyleClass().add("MBTNClick");
            returnMBTN.getStyleClass().remove("MBTNClick");
            saleMBTN.getStyleClass().remove("MBTNClick");
            stockMBTN.getStyleClass().remove("MBTNClick");
            queryMBTN.getStyleClass().remove("MBTNClick");
            staffMBTN.getStyleClass().remove("MBTNClick");
            customerMBTN.getStyleClass().remove("MBTNClick");

            stockMBTN.setDisable(false);
            saleMBTN.setDisable(false);
            returnMBTN.setDisable(false);
            settingMBTN.setDisable(true);
            queryMBTN.setDisable(false);
            staffMBTN.setDisable(false);
            customerMBTN.setDisable(false);

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().add(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().remove(customerTab);
        });

        queryMBTN.setOnMouseClicked(event -> {
            System.out.printf("queryClick");
            queryMBTN.getStyleClass().add("MBTNClick");
            returnMBTN.getStyleClass().remove("MBTNClick");
            saleMBTN.getStyleClass().remove("MBTNClick");
            settingMBTN.getStyleClass().remove("MBTNClick");
            stockMBTN.getStyleClass().remove("MBTNClick");
            staffMBTN.getStyleClass().remove("MBTNClick");
            customerMBTN.getStyleClass().remove("MBTNClick");

            stockMBTN.setDisable(false);
            saleMBTN.setDisable(false);
            settingMBTN.setDisable(false);
            returnMBTN.setDisable(false);
            queryMBTN.setDisable(true);
            staffMBTN.setDisable(false);
            customerMBTN.setDisable(false);
            returnMBTN.setDisable(false);

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().add(queryTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().remove(customerTab);
        });

        staffMBTN.setOnMouseClicked(event -> {
            System.out.printf("StaffClick");
            saleMBTN.getStyleClass().remove("MBTNClick");
            returnMBTN.getStyleClass().remove("MBTNClick");
            staffMBTN.getStyleClass().add("MBTNClick");
            settingMBTN.getStyleClass().remove("MBTNClick");
            queryMBTN.getStyleClass().remove("MBTNClick");
            stockMBTN.getStyleClass().remove("MBTNClick");
            customerMBTN.getStyleClass().remove("MBTNClick");

            stockMBTN.setDisable(false);
            saleMBTN.setDisable(false);
            settingMBTN.setDisable(false);
            returnMBTN.setDisable(false);
            queryMBTN.setDisable(false);
            staffMBTN.setDisable(true);
            customerMBTN.setDisable(false);

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().add(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().remove(customerTab);
        });

        customerMBTN.setOnMouseClicked(event -> {
            System.out.printf("StaffClick");
            saleMBTN.getStyleClass().remove("MBTNClick");
            returnMBTN.getStyleClass().remove("MBTNClick");
            staffMBTN.getStyleClass().remove("MBTNClick");
            settingMBTN.getStyleClass().remove("MBTNClick");
            queryMBTN.getStyleClass().remove("MBTNClick");
            stockMBTN.getStyleClass().remove("MBTNClick");
            customerMBTN.getStyleClass().add("MBTNClick");

            stockMBTN.setDisable(false);
            saleMBTN.setDisable(false);
            settingMBTN.setDisable(false);
            returnMBTN.setDisable(false);
            queryMBTN.setDisable(false);
            staffMBTN.setDisable(false);
            customerMBTN.setDisable(true);

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().add(customerTab);
        });

        asNewSBTN.setOnMouseClicked(event -> {
            System.out.printf("StaffClick");

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().remove(customerTab);
            centreTabP.getTabs().add(addSTab);
        });

        asExitBTN.setOnMouseClicked(event -> {
            System.out.printf("StaffClick");

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().add(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().remove(customerTab);
            centreTabP.getTabs().remove(addSTab);
        });

        newCusBTN.setOnMouseClicked(event -> {
            System.out.printf("StaffClick");

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().add(addCTab);
            centreTabP.getTabs().remove(customerTab);
            centreTabP.getTabs().remove(addSTab);
        });

        acCusDExitBTN.setOnMouseClicked(event -> {
            System.out.printf("StaffClick");

            centreTabP.getTabs().remove(stockTab);
            centreTabP.getTabs().remove(returnTab);
            centreTabP.getTabs().remove(saleTab);
            centreTabP.getTabs().remove(settingTab);
            centreTabP.getTabs().remove(queryTab);
            centreTabP.getTabs().remove(staffTab);
            centreTabP.getTabs().remove(addSTab);
            centreTabP.getTabs().remove(addCTab);
            centreTabP.getTabs().add(customerTab);
            centreTabP.getTabs().remove(addSTab);
        });

    }

    public void saleButtons() {

        s1BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("1");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("1");
                sFunds = 1.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "1");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s2BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("2");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("2");
                sFunds = 2.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "2");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s3BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("3");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("3");
                sFunds = 3.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "3");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s4BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("4");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("4");
                sFunds = 4.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "4");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s5BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("5");
            Double sFunds;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("5");
                sFunds = 5.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "5");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s6BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("6");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("6");
                sFunds = 6.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "6");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s7BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("7");

            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("7");
                sFunds = 7.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "7");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s8BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("8");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("8");
                sFunds = 8.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "8");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s9BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("9");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("9");
                sFunds = 9.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "9");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else if (sFunds > totalSPrice) {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }

        });

        s0BTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf("0");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("0");
                sFunds = 0.0;

            } else {


                sCashTF.setText(sCashTF.getText() + "0");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else if (sFunds > totalSPrice) {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }
        });

        sPBTN.setOnMouseClicked(event -> {
            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            System.out.printf(".");
            Double sFunds = null;

            if (sCashTF.getText().isEmpty()) {

                sCashTF.setText("0.");
                sFunds = 0.0;

            } else {


                sCashTF.setText(sCashTF.getText() + ".");
                sFunds = Double.parseDouble(sCashTF.getText());
            }

            if (sFunds.equals(totalSPrice)) {

                sProceedBTN.getStyleClass().add("sProceedRBTN");

            } else if (sFunds > totalSPrice) {
                sProceedBTN.getStyleClass().remove("sProceedRBTN");
            }
        });

        sProceedBTN.setOnMouseClicked(event -> {

            long timeNow = Calendar.getInstance().getTimeInMillis();

            Timestamp clickTstamp = new Timestamp(timeNow);

            String clickTstamp1 = clickTstamp.toString().substring(0, 18);

            Double sFunds = Double.parseDouble(sCashTF.getText());
            try {
                if (sFunds == totalSPrice) {


                    PreparedStatement pst = null;

                    Connection connection = Model.dbConnection.connectDatabase();

                    String insertSql = "INSERT INTO receipt(receiptID, staffID, productID, ProName, proPrice, proQuantity, saleDate) VALUES(?,?,?,?,?,?,current_timestamp)";

                    String insertFoundSql = "INSERT INTO receipt(receiptID, staffID, productID, ProName, proPrice, proQuantity, saleDate) VALUES(?,?,?,?,?,?,?)";

                    pst = connection.prepareStatement(insertSql);

                    PreparedStatement pstCheck = connection.prepareStatement(insertFoundSql);

                    for (int count = 0; customerBasket.size() > count; count++) {

                        Timestamp tstamp = null;

                        ResultSet rs;

                        String sql = "SELECT * FROM receipt";

                        rs = connection.createStatement().executeQuery(sql);

                        int basketQ = customerBasket.get(count).getRcpProQuantity();

                        if (rs.next()) {
                            do {
                                tstamp = rs.getTimestamp(8);

                                String tstamp1 = tstamp.toString().substring(0, 18);

                                System.out.println(tstamp1);
                                // System.out.println("record time= " + tstamp + " - ");

                                //System.out.println("time stamp= " + clickTstamp);


                                if (clickTstamp1.equals(tstamp1)) {

                                    System.out.println("record found!");

                                    pstCheck.setInt(1, rs.getInt(1));
                                    pstCheck.setInt(2, stfId);
                                    pstCheck.setInt(3, customerBasket.get(count).getRcpProductID());
                                    pstCheck.setString(4, customerBasket.get(count).getRcpProName());
                                    pstCheck.setDouble(5, customerBasket.get(count).getRcpProPrice());
                                    pstCheck.setInt(6, customerBasket.get(count).getRcpProQuantity());
                                    pstCheck.setTimestamp(7, clickTstamp);
                                    pstCheck.execute();





                                       ResultSet rsQ;

                                       String sqlQ = "SELECT * FROM product_codes WHERE productID ='" + rs.getInt(4) + "'";

                                       rsQ = connection.createStatement().executeQuery(sqlQ);





                                          //while(rsQ.next()){

                                              // String codeUpdate = "UPDATE product_codes pc, products p SET pc.proStatus = 'INACTIVE', p.proQuantity = -1 WHERE p.productID ='" + rs.getInt(4) + "'AND pc.receiptID ='" + rs.getInt(1) + "' LIMIT "+basketQ + "";
                                               //PreparedStatement pstCodes = connection.prepareStatement(codeUpdate);

                                              // pstCodes.executeUpdate();
                                         // }






                                      // pstCodes.close();






                                } else if (rs.isLast()) {

                                    int newID = rs.getInt(1) + 1;

                                    pstCheck.setInt(1, newID);
                                    pstCheck.setInt(2, stfId);
                                    pstCheck.setInt(3, customerBasket.get(count).getRcpProductID());
                                    pstCheck.setString(4, customerBasket.get(count).getRcpProName());
                                    pstCheck.setDouble(5, customerBasket.get(count).getRcpProPrice());
                                    pstCheck.setInt(6, customerBasket.get(count).getRcpProQuantity());
                                    pstCheck.setTimestamp(7, clickTstamp);

                                    pstCheck.execute();

                                    String codeUpdate = "UPDATE product_codes pc, products p SET pc.proStatus = 'INACTIVE', p.proQuantity = -1 WHERE p.productID ='" + rs.getInt(4) + "'AND pc.receiptID ='" + rs.getInt(1) + "'";
                                    PreparedStatement pstCodes = connection.prepareStatement(codeUpdate);

                                    pstCodes.executeUpdate();

                                    System.out.println("record added!");
                                }
                            } while (rs.next());
                        } else {
                            int newID = 1;

                            pst.setInt(1, newID);
                            pst.setInt(2, stfId);
                            pst.setInt(3, customerBasket.get(count).getRcpProductID());
                            pst.setString(4, customerBasket.get(count).getRcpProName());
                            pst.setDouble(5, customerBasket.get(count).getRcpProPrice());
                            pst.setInt(6, customerBasket.get(count).getRcpProQuantity());

                            pst.execute();

                            String codeUpdate = "UPDATE product_codes pc, products p SET pc.proStatus = 'INACTIVE', p.proQuantity = -1 WHERE p.productID ='" + rs.getInt(4) + "'AND pc.receiptID ='" + rs.getInt(1) + "' LIMIT "+basketQ + "";
                            PreparedStatement pstCodes = connection.prepareStatement(codeUpdate);

                            pstCodes.executeUpdate();

                            System.out.println("record  is added!");
                        }

                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Database Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Order Completed!");
                    alert.showAndWait();

                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        });

        sClearBTN.setOnMouseClicked(event -> {
            System.out.printf("Clear");

            sProceedBTN.getStyleClass().remove("sProceedRBTN");
            sCashTF.setText("");

        });

        saleClearBTN.setOnMouseClicked(event -> {
            System.out.printf("Order Clear");

            customerBasket.clear();
            sTotalPriceTF.clear();
            sCustomerTF.clear();

        });

    }

    public void returnButtons() {

        Connection connection = Model.dbConnection.connectDatabase();

        r1BTN.setOnMouseClicked(event -> {

            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";

                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("1");
                int sPassword = 0;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("1");
                    sPassword = 1;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "1");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }


        });

        r2BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";

                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("1");
                int sPassword = 0;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("2");
                    sPassword = 2;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "2");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        r3BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";

                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("1");
                int sPassword = 0;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("3");
                    sPassword = 3;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "3");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        r4BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";
                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("4");
                int sPassword = 4;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("4");
                    sPassword = 4;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "4");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

        r5BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";
                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("5");
                int sPassword = 5;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("5");
                    sPassword = 5;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "5");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

        r6BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";
                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("6");
                int sPassword = 6;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("6");
                    sPassword = 6;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "6");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        r7BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";
                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("7");
                int sPassword = 7;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("7");
                    sPassword = 7;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "7");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

        r8BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";
                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("8");
                int sPassword = 8;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("8");
                    sPassword = 8;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "8");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

        r9BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";
                rProceedBTN.getStyleClass().remove("rProceedRBTN");
                System.out.printf("9");
                int sPassword = 9;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("9");
                    sPassword = 9;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "9");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

        r0BTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";

                rProceedBTN.getStyleClass().remove("sProceedRBTN");
                System.out.printf("0");
                int sPassword = 0;

                if (rtnPasswordTF.getText().isEmpty()) {

                    rtnPasswordTF.setText("0");
                    sPassword = 0;

                } else {

                    sPassword = Integer.parseInt(rtnPasswordTF.getText());
                    rtnPasswordTF.setText(rtnPasswordTF.getText() + "0");
                }

                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {

                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        rProceedBTN.setOnMouseClicked(event -> {
            try {

                ResultSet rsCheck;
                String sql = "SELECT * FROM staff WHERE staffRole = 'Manager'";
                rsCheck = connection.prepareStatement(sql).executeQuery();

                while (rsCheck.next()) {
                    if (rtnPasswordTF.getText().equals(rsCheck.getInt("stfPin"))) {
                        rProceedBTN.getStyleClass().add("sProceedRBTN");

                        String orderSql = "UPDATE product_codes pc, products p SET pc.proStatus = 'ACTIVE', p.proQuantity = +1 WHERE p.receiptID ='" + rtnReceiptIDTF.getText() + "' AND WHERE pc.receiptID ='" + rtnReceiptIDTF.getText() + "'";

                        PreparedStatement pst = connection.prepareStatement(orderSql);
                        pst.executeUpdate();
                        pst.close();


                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Database Information");
                        alert.setHeaderText(null);
                        alert.setContentText("Order Completed!");
                        alert.showAndWait();

                    } else {
                        rProceedBTN.getStyleClass().remove("sProceedRBTN");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

        });

        rClearBTN.setOnMouseClicked(event -> {
            System.out.printf("Clear");

            rProceedBTN.getStyleClass().remove("sProceedRBTN");
            rtnPasswordTF.setText("");

        });

        returnClearBTN.setOnMouseClicked(event -> {
            System.out.printf("Return Clear");

            returnBasket.clear();

        });

    }

    public void getSearchDetails() {

        Connection connection = Model.dbConnection.connectDatabase();

        String sql2 = "SELECT * FROM product_codes WHERE proBarcode ='" + sProductIDTF.getText() + "'";
        ResultSet rs2;


        try {

            if (!sProductIDTF.getText().equals("")) {

                rs2 = connection.createStatement().executeQuery(sql2);


                if (rs2.next()) {

                    System.out.printf("Search found.");

                    sProductIDTF.setText(rs2.getString("productID"));

                } else {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Database Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Unkown Product Code!");
                    alert.showAndWait();

                }

                rs2.close();

                String sql = "SELECT * FROM products WHERE productID ='" + sProductIDTF.getText() + "'";
                ResultSet rs;
                rs = connection.createStatement().executeQuery(sql);

                if (rs.next()) {
                    sProductNameTF.setText(rs.getString("proName"));
                    sDescriptionTF.setText(rs.getString("proDescription"));
                    ProPriceTF.setText("£ " + rs.getString("proPrice"));
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Database Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Unkown Product!");
                    alert.showAndWait();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getSearchRDetails() {

        Connection connection = Model.dbConnection.connectDatabase();

        String sql2 = "SELECT * FROM product_codes WHERE proBarcode ='" + rtnReceiptIDTF.getText() + "'";
        ResultSet rs1;

        try {

            if (!rtnReceiptIDTF.getText().equals("")) {

                rs1 = connection.createStatement().executeQuery(sql2);

                if (rs1.next()) {

                    System.out.printf("Search found.");
                    rtnReceiptIDTF.setText(rs1.getString("productID"));
                } else {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Return Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Unkown Product Code!");
                    alert.showAndWait();
                }

                rs1.close();

                String sql = "SELECT * FROM receipt WHERE receiptID ='" + rtnReceiptIDTF.getText() + "'";
                ResultSet rs;
                rs = connection.createStatement().executeQuery(sql);

                if (rs.next()) {

                    System.out.printf("Search found.");

                    rtnCustomerIDTF.setText(rs.getString("customerID"));
                    rtnStaffIDTF.setText(rs.getString("staffID"));
                    rtnDateTF.setText("" + rs.getDate("saleDate"));

                    customerRBasket();
                } else {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Database Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Unkown Order!");
                    alert.showAndWait();
                }

                rs.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void clearSearchDetails(ActionEvent actionEvent) {

        sProductIDTF.setText("");
        sAmountTF.setText("");
        sProductNameTF.setText("");
        sDescriptionTF.setText("");
        ProPriceTF.setText("");

    }

    public void customerBasket(ActionEvent actionEvent) {
        Connection connection = Model.dbConnection.connectDatabase();
        String sql = "SELECT * FROM products WHERE productID ='" + sProductIDTF.getText() + "'";
        ResultSet rs;
        totalSPrice = 0;

        //DATETIME - format: YYYY-MM-DD HH:MI:SS

        long timeNow = Calendar.getInstance().getTimeInMillis();
        Timestamp tstamp = new Timestamp(timeNow);

        System.out.println("ts " + tstamp);
        try {

            rs = connection.createStatement().executeQuery(sql);

            if (rs.next()) {

                System.out.printf("Search found.");

                customerBasket.add(new receiptClass(1, 1, staffID, rs.getInt("productID"), rs.getString("proName"), rs.getDouble("proPrice"), Integer.parseInt(sAmountTF.getText()), tstamp));

            }

            rs.close();

            tcOrderID.setCellValueFactory(new PropertyValueFactory<>("rcpProductID"));
            tcOrderID.setMinWidth(100);

            tcOrderProName.setCellValueFactory(new PropertyValueFactory<>("rcpProName"));
            tcOrderProName.setMinWidth(100);

            tcOrderPrice.setCellValueFactory(new PropertyValueFactory<>("rcpProPrice"));
            tcOrderPrice.setMinWidth(75);

            tcOrderQuantity.setCellValueFactory(new PropertyValueFactory<>("rcpProQuantity"));
            tcOrderQuantity.setMinWidth(75);

            tvOrderDetails.setItems(null);
            tvOrderDetails.setMinHeight(200);
            tvOrderDetails.setItems(customerBasket);

            int countS = customerBasket.size();

            for (int count = 0; count < countS; count++) {

                totalSPrice = totalSPrice + (customerBasket.get(count).getRcpProPrice() * Integer.parseInt(sAmountTF.getText()));

            }


            sTotalPriceTF.setText("Total = " + Double.toString(totalSPrice));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void customerRBasket() {
        Connection connection = Model.dbConnection.connectDatabase();
        String sql = "SELECT * FROM receipt WHERE receiptID ='" + rtnReceiptIDTF.getText() + "'";
        ResultSet rs;
        totalRPrice = 0;

        long timeNow = Calendar.getInstance().getTimeInMillis();
        Timestamp tstamp = new Timestamp(timeNow);

        System.out.println("ts " + tstamp);
        try {

            rs = connection.createStatement().executeQuery(sql);

            while (rs.next()) {

                System.out.printf("Search found.");

                returnBasket.add(new receiptClass(rs.getInt("receiptID"), rs.getInt("customerID"), rs.getInt("staffID"), rs.getInt("productID"), rs.getString("proName"), rs.getDouble("proPrice"), rs.getInt("proQuantity"), rs.getTimestamp("saleDate")));

            }

            rs.close();

            rtnProductIDClm.setCellValueFactory(new PropertyValueFactory<>("rcpProductID"));
            rtnProductIDClm.setMinWidth(100);

            rtnProNameClm.setCellValueFactory(new PropertyValueFactory<>("rcpProName"));
            rtnProNameClm.setMinWidth(100);

            rtnProPriceClm.setCellValueFactory(new PropertyValueFactory<>("rcpProPrice"));
            rtnProPriceClm.setMinWidth(75);

            rtnProQuantityClm.setCellValueFactory(new PropertyValueFactory<>("rcpProQuantity"));
            rtnProQuantityClm.setMinWidth(75);

            tvReturnDetails.setItems(null);
            tvReturnDetails.setMinHeight(200);
            tvReturnDetails.setItems(returnBasket);

            int countS = returnBasket.size();

            for (int count = 0; count < countS; count++) {

                totalRPrice = totalRPrice + (returnBasket.get(count).getRcpProPrice() * returnBasket.get(count).getRcpProQuantity());

            }


            rtnTotalPTF.setText(Double.toString(totalRPrice));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearCusBasket(ActionEvent actionEvent) {

        System.out.printf("Basket Cleared.");

        customerBasket.clear();


        tcOrderID.setCellValueFactory(new PropertyValueFactory<>("rcpProductID"));
        tcOrderID.setMinWidth(100);

        tcOrderProName.setCellValueFactory(new PropertyValueFactory<>("rcpProName"));
        tcOrderProName.setMinWidth(100);

        tcOrderPrice.setCellValueFactory(new PropertyValueFactory<>("rcpProPrice"));
        tcOrderPrice.setMinWidth(75);

        tcOrderQuantity.setCellValueFactory(new PropertyValueFactory<>("rcpProQuantity"));
        tcOrderQuantity.setMinWidth(75);

        tvOrderDetails.setItems(null);
        tvOrderDetails.setMinHeight(200);
        tvOrderDetails.setItems(customerBasket);


    }

    public void clearProDetails(ActionEvent actionEvent) {

        stkProNameTf.setText("");
        stkProDescriptionTa.setText("");
        stkProPriceTf.setText("");
    }

    public void searchQuery(ActionEvent actionEvent) throws SQLException, ParseException {

        qResultsOL.clear();
        String searchBy = qSearchCMB.getValue();
        LocalDate qFromDate = qFromDateDP.getValue();
        LocalDate qToDate = qToDateDP.getValue();
        LocalDate nullDate = null;
        Double totalQP = 0.0;

        String sSearchSQL = "SELECT receiptID, customerID, staffID, productID,ProName, proPrice, SUM(proQuantity) AS rsProQuantity,saleDate FROM receipt GROUP BY productID";
        String sSearchSQL2 = "SELECT receiptID, customerID, staffID, productID,ProName, proPrice, SUM(proQuantity) AS rsProQuantity,saleDate FROM receipt GROUP BY productID";


        Connection connection = Model.dbConnection.connectDatabase();
        ResultSet rs = null;

        System.out.println("search by " + searchBy.toString() + qFromDateDP.getValue().toString());

        switch (searchBy) {

            case "Most Popular":

                if (qToDate == nullDate) {

                    rs = connection.createStatement().executeQuery(sSearchSQL);

                    while (rs.next()) {

                        System.out.println("search date is: " + rs.getTimestamp("saleDate").toString().substring(0, 10) + "with: " + qFromDate.toString().substring(0, 10));

                        if (rs.getTimestamp("saleDate").toString().substring(0, 10).equals(qFromDate.toString().substring(0, 10))) {

                            totalQP = totalQP + (rs.getDouble("proPrice") * rs.getInt("rsProQuantity"));

                            qResultsOL.add(new receiptClass(rs.getInt("receiptID"), rs.getInt("customerID"), rs.getInt("staffID"), rs.getInt("productID"), rs.getString("proName"), rs.getDouble("proPrice"), rs.getInt("rsProQuantity"), rs.getTimestamp("saleDate")));

                        } else {
                            System.out.println("no match!");
                        }
                        qResultsTPriceTF.setText("£"+totalQP);
                    }

                    rs.close();

                } else {

                    rs = connection.createStatement().executeQuery(sSearchSQL);


                    while (rs.next()) {



                        DateFormat sdf = new SimpleDateFormat("yyyy/dd/MM");
                        Date date = java.sql.Date.valueOf(qFromDate.toString().substring(0, 10));
                        Date date1 = java.sql.Date.valueOf(qToDate.toString().substring(0, 10));

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        cal.add(Calendar.DATE, -360);




                        //Date
                       // Date dateBefore365Days1 = sdf.format(dateBefore365Days.getTime());

                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(date1);
                        cal2.add(Calendar.DATE, -360);

                        Date rsDate = java.sql.Date.valueOf(rs.getTimestamp("saleDate").toString().substring(0, 10));

                        String sSearchDate = "SELECT * FROM receipt";

                        System.out.println("if date: " + date + " with " + date1 + " rs time: " + rsDate);

                        if (rsDate.after(date) && rsDate.before(date1)) {

                            totalQP = totalQP + (rs.getDouble("proPrice") * rs.getInt("rsProQuantity"));



                            qResultsOL.add(new receiptClass(rs.getInt("receiptID"), rs.getInt("customerID"), rs.getInt("staffID"), rs.getInt("productID"), rs.getString("proName"), rs.getDouble("proPrice"), rs.getInt("rsProQuantity"), rs.getTimestamp("saleDate")));

                            XYChart.Series chartItem1 = new XYChart.Series<>();

                            String chrtPro = rs.getString("proName");
                            int chrtQuantity = rs.getInt("rsProQuantity");

                            chartItem1.getData().add(new XYChart.Data<>(chrtPro,chrtQuantity));
                            queryChart.setAnimated(false);
                            queryChart.getData().add(chartItem1);


                        } else {
                            System.out.println("no match!");
                        }

                        ResultSet rsD = connection.createStatement().executeQuery(sSearchDate);



                        while(rsD.next()){

                            Date rsDDate = java.sql.Date.valueOf(rsD.getTimestamp("saleDate").toString().substring(0, 10));

                            System.out.println("found: "+rsDDate);
                            System.out.println("filter: "+cal.getTime());
                            if (rsDDate.after(cal.getTime()) && rsDDate.before(cal2.getTime()) ) {

                                System.out.printf("found " + rsD.getInt("receiptID") + " Date " + rsD.getDate("saleDate"));

                                String chrtPro2 = rsD.getString("proName");
                                int chrtQuantity2 = rsD.getInt("proQuantity");

                                XYChart.Series chartItem2 = new XYChart.Series<>();

                                chartItem2.getData().add(new XYChart.Data<>(chrtPro2,chrtQuantity2));
                                recordChart.setAnimated(false);
                                recordChart.getData().add(chartItem2);

                            }
                        }

                        qResultsTPriceTF.setText("£"+totalQP);
                    }


                    rs.close();

                }

                qResultsProIDTC.setCellValueFactory(new PropertyValueFactory<>("rcpProductID"));
                qResultsProIDTC.setMinWidth(50);

                qResultsNameTC.setCellValueFactory(new PropertyValueFactory<>("rcpProName"));
                qResultsNameTC.setMinWidth(100);

                qResultsPPriceTC.setCellValueFactory(new PropertyValueFactory<>("rcpProPrice"));
                qResultsPPriceTC.setMinWidth(75);

                qResultsQuantityTC.setCellValueFactory(new PropertyValueFactory<>("rcpProQuantity"));
                qResultsQuantityTC.setMinWidth(100);

                qResultsTV.setItems(null);
                qResultsTV.setMinHeight(200);
                qResultsTV.setItems(qResultsOL);

                break;

            case "Least Popular":

                if (qToDate == nullDate) {

                    rs = connection.createStatement().executeQuery(sSearchSQL2);

                    while (rs.next()) {

                        System.out.println("search date is: " + rs.getTimestamp("saleDate").toString().substring(0, 10) + "with: " + qFromDate.toString().substring(0, 10));

                        if (rs.getTimestamp("saleDate").toString().substring(0, 10).equals(qFromDate.toString().substring(0, 10))) {

                            totalQP = totalQP + (rs.getDouble("proPrice") * rs.getInt("rsProQuantity"));
                            qResultsOL.add(new receiptClass(rs.getInt("receiptID"), rs.getInt("customerID"), rs.getInt("staffID"), rs.getInt("productID"), rs.getString("proName"), rs.getDouble("proPrice"), rs.getInt("rsProQuantity"), rs.getTimestamp("saleDate")));

                        }

                        qResultsTPriceTF.setText("£"+totalQP);
                    }

                    rs.close();

                } else {

                    rs = connection.createStatement().executeQuery(sSearchSQL);

                    int rsSize = 0;

                    while (rs.next()) {

                        System.out.println("search date is: " + rs.getTimestamp("saleDate").toString().substring(0, 10) + "with: " + qFromDate.toString().substring(0, 10));

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
                        Date date = java.sql.Date.valueOf(qFromDate.toString().substring(0, 10));
                        Date date1 = java.sql.Date.valueOf(qToDate.toString().substring(0, 10));


                        Date rsDate = java.sql.Date.valueOf(rs.getTimestamp("saleDate").toString().substring(0, 10));

                        System.out.println("if date: " + date + " with " + date1 + "rs time: " + rsDate);

                        if (rsDate.after(date) || rsDate.before(date1)) {

                            totalQP = totalQP + (rs.getDouble("proPrice") * rs.getInt("rsProQuantity"));

                            qResultsOL.add(new receiptClass(rs.getInt("receiptID"), rs.getInt("customerID"), rs.getInt("staffID"), rs.getInt("productID"), rs.getString("proName"), rs.getDouble("proPrice"), rs.getInt("rsProQuantity"), rs.getTimestamp("saleDate")));

                        } else {
                            System.out.println("no match!");
                        }

                        qResultsTPriceTF.setText("£"+totalQP);

                    }

                    rs.close();

                }

                qResultsProIDTC.setCellValueFactory(new PropertyValueFactory<>("rcpProductID"));
                qResultsProIDTC.setMinWidth(50);

                qResultsNameTC.setCellValueFactory(new PropertyValueFactory<>("rcpProName"));
                qResultsNameTC.setMinWidth(100);

                qResultsPPriceTC.setCellValueFactory(new PropertyValueFactory<>("rcpProPrice"));
                qResultsPPriceTC.setMinWidth(75);

                qResultsQuantityTC.setCellValueFactory(new PropertyValueFactory<>("rcpProQuantity"));
                qResultsQuantityTC.setMinWidth(100);

                qResultsTV.setItems(null);
                qResultsTV.setMinHeight(200);
                qResultsTV.setItems(qResultsOL);

                break;

            default:

                System.out.println("Please select search!");
        }

    }

}
