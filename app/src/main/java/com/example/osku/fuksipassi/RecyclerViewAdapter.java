package com.example.osku.fuksipassi;



import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Osku on 11.4.2018.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<String> mListTitle = new ArrayList<>();
    private List<String> mListDesc = new ArrayList<>();
    private List<String> mListDates = new ArrayList<>();
    Runnable callback;
    private String fragment_tag;

    // Constructor for RecyclerViewAdapter, has callback parameter to allow updating list.
    public RecyclerViewAdapter(List<List<String>> super_list, String tag, Runnable callback) {
        this.mListTitle = super_list.get(0);
        this.mListDesc = super_list.get(1);
        if(tag.equals ("completed")){
            this.mListDates = super_list.get(2);
        }
        if(callback != null){
            this.callback = callback;
        }
        fragment_tag = tag;
    }

    // Overloaded constructor without callback.S
    public RecyclerViewAdapter(List<List<String>> super_list, String tag) {
        this.mListTitle = super_list.get(0);
        this.mListDesc = super_list.get(1);
        if(tag.equals("completed")){
            this.mListDates = super_list.get(2);
        }

        fragment_tag = tag;
    }



    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new RecyclerViewHolder(inflater, parent, fragment_tag,callback);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.mTitleText.setText(mListTitle.get(position));
        holder.mDescText.setText(mListDesc.get(position));
        if (fragment_tag.equals("completed")) {
            holder.mDateText.setText(mListDates.get(position));
        }

    }
    // Returns the number of challenges on the list.
    @Override
    public int getItemCount() {
        return mListTitle.size();
    }


}

class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private String outputString;
    public TextView mTitleText, mDescText, mDateText, popupTitle;
    public EditText mUserName,mPassword;
    FrameLayout frameLayout;
    public Button mConfButton, popCancelBtn, popAcceptBtn;
    public RecyclerViewHolder(final LayoutInflater inflater, final ViewGroup container, String tag, final Runnable callback) {


        // Inflating the card layout depending on the tag parameter.
        super(inflater.inflate
                ((tag.equals("challenges")) ? R.layout.card_view_chall : R.layout.card_view_comp, container,
                        false));


        mTitleText = itemView.findViewById(R.id.title_holder);
        mDescText = itemView.findViewById(R.id.desc_holder);
        mDateText = itemView.findViewById(R.id.date_holder);
        frameLayout = itemView.findViewById(R.id.fragment_container);

        // If the app is in the challenge fragment, set up items and listeners.
        if (tag.equals("challenges")) {
            mConfButton = itemView.findViewById(R.id.card_conf_button);
            mConfButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Setting the layout inflater for popup window.
                    LayoutInflater pInflater = (LayoutInflater) itemView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final ViewGroup container1 = (ViewGroup) pInflater.inflate(R.layout.confirmation_popup, (ViewGroup) itemView.findViewById(R.id.fragment_container), false);
                    final PopupWindow popupWindow = new PopupWindow(container1,(int)(container.getWidth()/1.5) ,(int)(container.getHeight()/2.2), true);

                    System.out.println(container.getRootView().getWidth());
                    popupWindow.setAnimationStyle(R.style.Animation);


                    popupTitle = container1.findViewById(R.id.popuptext);
                    popAcceptBtn = container1.findViewById(R.id.accept_button);
                    popCancelBtn = container1.findViewById(R.id.cancel_button);
                    mUserName = container1.findViewById(R.id.username);
                    mPassword = container1.findViewById(R.id.password);
                    popupTitle.setText(mTitleText.getText().toString());

                    // Dismisses the popup window
                    popCancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupWindow.dismiss();
                        }
                    });

                    // Click listener for dialog accept button.
                    popAcceptBtn.setOnClickListener(new View.OnClickListener() {
                        String date;
                        @Override
                        public void onClick(View view) {
                            List<String> list = new ArrayList<>();
                            list.add(mTitleText.getText().toString());
                            list.add(mDescText.getText().toString());
                            list.add(date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                            TempDataReader reader = new TempDataReader(itemView.getContext());

                            // Authorising credentials.
                            try {
                                outputString = encrypt(mUserName.getText().toString(),mPassword.getText().toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            // Performs necessary actions if credentials were correct.
                            if(reader.authorize(outputString)){
                                // Saving data from current card into the completed challenges list.
                                reader.saveFile(list);
                                // Removing the completed challenge from the list.
                                reader.removeChallenge(getAdapterPosition()+1);

                                // If callback is not null, run the callback.
                                if(callback != null){
                                    callback.run();
                                } else{
                                    Toast toast = Toast.makeText(itemView.getContext(), "Päivitä näkymä manuaalisesti oikeassa yläreunassa olevasta painikkeesta.", Toast.LENGTH_LONG);
                                    View toastView = toast.getView();
                                    toastView.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                                    TextView txt = toastView.findViewById(android.R.id.message);
                                    txt.setTextColor(Color.WHITE);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();
                                    // Disable the button so the user wont press it before updating the list.
                                    mConfButton.setEnabled(false);
                                    mConfButton.setClickable(false);
                                    mConfButton.setTextColor(Color.GRAY);
                                }
                                // Dismissing the popup window.
                                popupWindow.dismiss();
                                Toast toastRegistered = Toast.makeText(itemView.getContext(),"Tehtävä suoritettu onnistuneesti!", Toast.LENGTH_SHORT);
                                toastRegistered.show();
                            }
                            // Alert the user about wrong username or password.
                            else{
                                Toast toast = Toast.makeText(itemView.getContext(), "Väärä käyttäjätunnus tai salasana.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                    popupWindow.showAtLocation(itemView, Gravity.CENTER, 25, 100);
                    // Dimming the background.
                    dimBehind(popupWindow);

                }

            });

        }

    }
    // Checks if the username and password are correct.
    private String encrypt (String data,String pass) throws Exception{
        SecretKeySpec key = generateKey(pass);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] endVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(endVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte [] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return secretKeySpec;

    }

    // Dims the background when popupwindow is shown.
    public static void dimBehind(PopupWindow popupWindow) {
        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.4f;
        wm.updateViewLayout(container, p);
    }

}