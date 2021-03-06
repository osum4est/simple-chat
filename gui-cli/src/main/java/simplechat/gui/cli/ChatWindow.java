package main.java.simplechat.gui.cli;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import main.java.simplechat.core.interfaces.IChatConnection;
import main.java.simplechat.gui.cli.controls.SubmittableTextBox;
import main.java.simplechat.gui.cli.controls.UnfocusableTextBox;
import main.java.simplechat.core.model.Message;
import main.java.simplechat.core.model.User;
import main.java.simplechat.core.interfaces.IChatListener;

import java.util.Collections;

class ChatWindow extends BasicWindow implements IChatListener {
    private MultiWindowTextGUI gui;
    private IChatConnection connection;
    private UnfocusableTextBox chatTextBox;
    private SubmittableTextBox newMessageTextBox;

    ChatWindow(MultiWindowTextGUI gui) {
        super("Simple Chat");
        this.gui = gui;
        setHints(Collections.singletonList(Hint.FULL_SCREEN));

        Panel panel = new Panel(new BorderLayout());
        Panel centerPanel = new Panel(new BorderLayout());

        chatTextBox = new UnfocusableTextBox();
        chatTextBox.setReadOnly(true);
        chatTextBox.setLayoutData(BorderLayout.Location.CENTER);

        centerPanel.addComponent(chatTextBox);
        centerPanel.setLayoutData(BorderLayout.Location.CENTER);
        panel.addComponent(centerPanel);

        newMessageTextBox = new SubmittableTextBox();
        newMessageTextBox.setLayoutData(BorderLayout.Location.BOTTOM);
        newMessageTextBox.takeFocus();
        newMessageTextBox.addListener(this::sendMessage);

        panel.addComponent(newMessageTextBox.withBorder(Borders.singleLineBevel("Message:")));
        setComponent(panel);
    }

    @Override
    public void userJoined(User user) {
        addLogMessage(user.getName() + " has joined!");
    }

    @Override
    public void userLeft(User user) {
        addLogMessage(user.getName() + " has !");
    }

    @Override
    public void recvMessage(Message message) {
        chatTextBox.addLine(message.getSender().getName() + ": " + message.getContents());
    }

    @Override
    public void error(String message) {
        MessageDialog.showMessageDialog(gui, "Error!", message);
    }

    private void sendMessage() {
        connection.sendMessage(newMessageTextBox.getText());
        newMessageTextBox.setText("");
    }

    void connected(IChatConnection connection) {
        this.connection = connection;
        connection.registerListener(this);

        chatTextBox.setText("Welcome to Simple Chat! There are " + connection.getUsers().size() + " users connected:");
        for (User user : connection.getUsers())
            addLogMessage(user.getName());

        chatTextBox.addLine("");
    }

    private void addLogMessage(String message) {
        chatTextBox.addLine("* " + message);
    }
}
