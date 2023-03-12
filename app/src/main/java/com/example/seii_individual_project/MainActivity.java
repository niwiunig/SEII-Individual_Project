package com.example.seii_individual_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText matrikelnummerInput;
    private TextView responseView;
    private TextView errorInputTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matrikelnummerInput = findViewById(R.id.matrikelnummerInput);
        Button sendButton = findViewById(R.id.sendButton);
        responseView = findViewById(R.id.responseView);
        Button checkButton = findViewById(R.id.buttonEx2);
        errorInputTextView = findViewById(R.id.errorInputTextView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matrikelnummer = matrikelnummerInput.getText().toString();

                if(isNumeric(matrikelnummer)) {
                    errorInputTextView.setVisibility(View.GONE);
                    new ConnectTask().execute(matrikelnummer);
                }
                else {
                    errorInputTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matrikelNummer = matrikelnummerInput.getText().toString();

                if(isNumeric(matrikelNummer)) {
                    errorInputTextView.setVisibility(View.GONE);
                    checkNumbers(matrikelNummer);
                } else {
                    errorInputTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean isNumeric(String str) {
        if (str == null || str.length() < 1) {
            return false;
        }

        try{
            Double d = Double.parseDouble(str);
        } catch (NumberFormatException nfex){
            return false;
        }

        return true;
    }

    private void checkNumbers(String matrikelnummer) {
        int length = matrikelnummer.length();

        //iterate over all integers of the matrikelnummer and check for common divisor > 1
        for (int i = 0; i < length - 1; i++) {
            for (int j = i + 1; j < length; j++) {
                int num1 = Character.getNumericValue(matrikelnummer.charAt(i));
                int num2 = Character.getNumericValue(matrikelnummer.charAt(j));
                if (hasCommonFactor(num1, num2)) {
                    responseView.setText("Nummern " + (i + 1) + " und " + (j + 1) + " haben einen gemeinsamen Teiler > 1");
                    return;
                }
            }
        }

        responseView.setText("Kein Paar von Nummern gefunden die einen gemeinsamen Teiler > 1 vorweisen");
    }

    private boolean hasCommonFactor(int a, int b) {
        for (int i = 2; i <= Math.min(a, b); i++) {
            if (a % i == 0 && b % i == 0) {
                return true;
            }
        }
        return false;
    }

    private class ConnectTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {
                String serverAddress = "se2-isys.aau.at";
                int serverPort = 53212;

                //Open a socket to the server
                Socket socket = new Socket(serverAddress, serverPort);

                //create a read and a write stream
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String inputLine;

                out.println(params[0]);

                //read response
                while ((inputLine = in.readLine()) != null) {
                    response += inputLine;
                }

                //close streams and socket
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            responseView.setText(result);
        }
    }
}