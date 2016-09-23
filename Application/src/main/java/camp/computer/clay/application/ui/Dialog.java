package camp.computer.clay.application.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.model.profile.ExtensionProfile;

public class Dialog {

    private Launcher launcher = null;

    public Dialog(Launcher launcher) {
        this.launcher = launcher;
    }

    // TODO: Replace OnCompleteCallback with Action?
    public interface OnCompleteCallback<T> {
        void onComplete(T result);
    }

    public void promptInputText(final OnCompleteCallback onCompleteCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(launcher.getLauncherView());
        builder.setTitle("Create Extension");

        // Set up the input
        final EditText input = new EditText(launcher.getLauncherView());

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Add input to view
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCompleteCallback.onComplete(input.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Callback with "Cancel" action
                dialog.cancel();
            }
        });

        builder.show();
    }

    // TODO: public <T> void promptSelection(List<T> options, OnCompleteCallback onCompleteCallback) {
    public <T> void promptSelection(final List<ExtensionProfile> options, final OnCompleteCallback onCompleteCallback) {

        // Items
//        List<String> options = new ArrayList<>();
//        options.add("Servo");
//        options.add("Servo with Analog Feedback");
//        options.add("IR Rangefinder");
//        options.add("Ultrasonic Rangefinder");
//        options.add("Stepper Motor");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(launcher.getLauncherView());
        // dialogBuilder.setIcon(R.drawable.ic_launcher);
        dialogBuilder.setTitle("Select a patch to connect:");

        // Add data adapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                launcher.getLauncherView(),
                android.R.layout.select_dialog_item
        );

//        // Add data to adapter. These are the options.
//        for (int i = 0; i < options.size(); i++) {
//            arrayAdapter.add(options.get(i));
//        }

        // Add Profiles
        for (int i = 0; i < options.size(); i++) {
//            ExtensionProfile extensionProfile = getClay().getExtensionProfiles().get(i);
//            options.add(extensionProfile.getLabel());
            arrayAdapter.add(options.get(i).getLabel());
        }

        // Apply the adapter to the dialog
        dialogBuilder.setAdapter(arrayAdapter, null);

        /*
        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        */

        final AlertDialog dialog = dialogBuilder.create();

        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectionLabel = arrayAdapter.getItem(position);
                ExtensionProfile selection = options.get(position);

                // Configure based on Profile
                // Add Ports based on Profile
                onCompleteCallback.onComplete(selection);
//                while (selection.getPortCount() < position + 1) {
//                    selection.addPort(new Port());
//                }

                // Response
                /*
                AlertDialog.Builder builderInner = new AlertDialog.Builder(appContext);
                builderInner.setMessage(selectionLabel);
                builderInner.setTitle("Connecting patch");
                builderInner.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        });

                dialog.dismiss();
                builderInner.show();
                */

                dialog.dismiss();

                promptTasks();
            }
        });

        dialog.show();
    }

    // Break multi-step tasks up into a sequence of floating interface elements that must be completed to continue (or abandon the sequence)
    // displayFloatingTaskDialog(<task list>, <task step to display>)

    public void promptTasks() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(launcher.getLauncherView());
        // builderSingle.setIcon(R.drawable.ic_launcher);
        dialogBuilder.setTitle("Complete these steps to assemble");

        // TODO: Difficulty
        // TODO: Average Time

        // Create data adapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                launcher.getLauncherView(),
                android.R.layout.select_dialog_multichoice
        );

        // Add data to adapter
        arrayAdapter.add("Task 1");
        arrayAdapter.add("Task 2");
        arrayAdapter.add("Task 3");
        arrayAdapter.add("Task 4");
        arrayAdapter.add("Task 5");

        final Context appContext = launcher.getLauncherView();

        /*
        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        */

        // Positive button
        dialogBuilder.setPositiveButton(
                "Start",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        String strName = arrayAdapter.getItem(which);

                        // Response
                        /*
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(appContext);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Connecting patch");
                        builderInner.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                });

                        dialog.dismiss();
                        builderInner.show();
                        */

//                        promptTasks();


                        promptTask();
                    }
                });

        // Set data adapter
        dialogBuilder.setAdapter(
                arrayAdapter,
                null
        );


        AlertDialog dialog = dialogBuilder.create();

        dialog.getListView().setItemsCanFocus(false);
        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Manage selected items here
                System.out.println("clicked" + position);
                CheckedTextView textView = (CheckedTextView) view;
                if (textView.isChecked()) {

                } else {

                }
            }
        });

        dialog.show();
    }

    public void promptTask() {

        // Items
        List<String> options = new ArrayList<>();
        options.add("Task 1");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(launcher.getLauncherView());
        // dialogBuilder.setIcon(R.drawable.ic_launcher);
        dialogBuilder.setTitle("Do this task");

        // Add data adapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                launcher.getLauncherView(),
                android.R.layout.select_dialog_singlechoice
        );

        // Add data to adapter. These are the options.
        for (int i = 0; i < options.size(); i++) {
            arrayAdapter.add(options.get(i));
        }

        // Apply the adapter to the dialog
        dialogBuilder.setAdapter(arrayAdapter, null);

        /*
        // "Back" Button
        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        */

        final AlertDialog dialog = dialogBuilder.create();

        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String itemLabel = arrayAdapter.getItem(position);

                // Response
                /*
                AlertDialog.Builder builderInner = new AlertDialog.Builder(appContext);
                builderInner.setMessage(strName);
                builderInner.setTitle("Connecting patch");
                builderInner.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        });

                dialog.dismiss();
                builderInner.show();
                */

                dialog.dismiss();

                promptTask();
            }
        });

        dialog.show();
    }
}
