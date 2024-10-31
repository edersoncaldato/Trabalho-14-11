module josimar.trabalhoquartozeonze {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens dva to javafx.fxml;
    exports dva;
}