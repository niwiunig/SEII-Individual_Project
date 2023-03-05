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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matrikelnummerInput = findViewById(R.id.matrikelnummerInput);
        Button button = findViewById(R.id.button);
        responseView = findViewById(R.id.responseView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matrikelnummer = matrikelnummerInput.getText().toString();
                new ConnectTask().execute(matrikelnummer);
            }
        });
    }

    private class ConnectTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {
                String serverAddress = "se2-isys.aau.at";
                int serverPort = 53212;

                Socket socket = new Socket(serverAddress, serverPort);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String inputLine;

                out.println(params[0]);

                while ((inputLine = in.readLine()) != null) {
                    response += inputLine;
                }
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