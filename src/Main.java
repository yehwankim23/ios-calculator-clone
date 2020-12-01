import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;

public class Main extends Application {
    private String[] operands = new String[]{"0", null};
    private int cursor = 0;
    private Operation operation = null;
    private boolean isResult = false;

    @FXML
    private Label label;
    @FXML
    private Button clear;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (!System.getProperty("java.version").startsWith("1.8")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("iOS Calculator Clone");
            alert.setHeaderText("Error");
            alert.setContentText("Java SE 8 is required");
            alert.show();
            return;
        }

        primaryStage.setResizable(false);
        Class<Main> main = Main.class;

        primaryStage.getIcons()
                .add(new Image(Objects.requireNonNull(main.getResourceAsStream("icon.png"))));

        primaryStage.setTitle("iOS Calculator Clone");

        Scene scene = new Scene(
                FXMLLoader.load(Objects.requireNonNull(main.getResource("layout" + ".fxml"))));

        scene.getStylesheets()
                .add(Objects.requireNonNull(main.getResource("style.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void onMouseClicked(MouseEvent mouseEvent) {
        Labeled control = (Labeled) mouseEvent.getSource();

        if (control.equals(label)) {
            operands[cursor] = operands[cursor].replace("-", "").length() == 1 ? "0"
                    : operands[cursor].substring(0, operands[cursor].length() - 1);

            setLabelText(operands[cursor]);
            return;
        }

        String controlId = control.getId();
        char controlText = control.getText().charAt(0);

        switch (controlId) {
            case "clear":
                if (controlText == 'A') {
                    operands = new String[]{"0", null};
                    cursor = 0;
                    operation = null;
                    isResult = false;
                } else {
                    operands[cursor] = "0";
                }

                setLabelText(operands[cursor]);
                clear.setText("AC");
                break;
            case "changeSign":
                operands[cursor] = getResult(operands[cursor], Operation.MULTIPLICATION, "-1");
                setLabelText(operands[cursor]);
                break;
            case "percent":
                operands[cursor] = getResult(operands[cursor], Operation.DIVISION, "100");
                setLabelText(operands[cursor]);
                break;
            case "addition":
            case "subtraction":
            case "multiplication":
            case "division":
                if (!isResult && operands[1] != null) {
                    operands[0] = getResult(operands[0], operation, operands[1]);
                    setLabelText(operands[0]);
                }

                operation = Operation.valueOf(controlId.toUpperCase());
                operands[1] = "0";
                cursor = 1;
                break;
            case "result":
                if (operands[1] == null) {
                    break;
                }

                operands[0] = getResult(operands[0], operation, operands[1]);
                isResult = true;
                setLabelText(operands[0]);
                break;
            case "decimalPoint":
                if (operands[cursor].contains(".")) {
                    break;
                }

                if (isResult) {
                    operands[0] = "0";
                    cursor = 0;
                    isResult = false;
                }

                operands[cursor] += ".";
                setLabelText(operands[cursor]);
                clear.setText("C");
                break;
            default:
                if (isResult) {
                    operands[0] = "0";
                    cursor = 0;
                    isResult = false;
                }

                operands[cursor] = (operands[cursor].equals("0") ? "" : operands[cursor])
                        + controlText;

                setLabelText(operands[cursor]);
                clear.setText("C");
                break;
        }
    }

    private void setLabelText(String text) {
        label.setText(new DecimalFormat("#,##0.########").format(Double.parseDouble(text)) + (
                text.endsWith(".") ? "." : ""));
    }

    private String getResult(String leftOperand, Operation operation, String rightOperand) {
        double leftOperandDouble = Double.parseDouble(leftOperand);
        double rightOperandDouble = Double.parseDouble(rightOperand);

        switch (operation) {
            case ADDITION:
                return String.valueOf(leftOperandDouble + rightOperandDouble);
            case SUBTRACTION:
                return String.valueOf(leftOperandDouble - rightOperandDouble);
            case MULTIPLICATION:
                return String.valueOf(leftOperandDouble * rightOperandDouble);
            case DIVISION:
                if (rightOperandDouble != 0) {
                    return String.valueOf(leftOperandDouble / rightOperandDouble);
                }
        }

        return "0";
    }

    private enum Operation {
        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION
    }
}
