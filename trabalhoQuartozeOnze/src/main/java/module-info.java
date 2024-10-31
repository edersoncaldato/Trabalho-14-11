module josimar.trabalhoquartozeonze {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens josimar.trabalhoquartozeonze to javafx.fxml;
    exports josimar.trabalhoquartozeonze;
}