package my.edu.tarc.mqttlight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    MqttHelper mqttHelper;
    private ToggleButton toggleButtonOnOff;
    private TextView textViewMessage;
    private ImageView imageViewBulb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButtonOnOff = findViewById(R.id.toggleButtonOnOff);
        imageViewBulb = findViewById(R.id.imageViewBulb);
        textViewMessage = findViewById(R.id.textViewMessage);

        toggleButtonOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               sendMessage(isChecked);
            }
        });

        startMqtt();
    }

    private void sendMessage(boolean isChecked) {
        String light_state;

        if(isChecked)
            light_state = "on";
        else
            light_state = "off";

        changeLightState(light_state);
        mqttHelper.publishToTopic(0, light_state);
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                textViewMessage.setText(s);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                textViewMessage.setText("Connection Lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                if(topic.equals("sensor/light")){
                    String light_state = mqttMessage.toString();
                    changeLightState(light_state);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    private void changeLightState(String light_state) {
        if(light_state.equals("on")){
            imageViewBulb.setImageResource(R.drawable.light_on);
            toggleButtonOnOff.setChecked(true);
        }else{
            imageViewBulb.setImageResource(R.drawable.light_off);
            toggleButtonOnOff.setChecked(false);
        }
    }

}
