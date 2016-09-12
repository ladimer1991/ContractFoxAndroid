package com.example.jakubkalinowski.contractfoxandroid.Navigation_Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jakubkalinowski.contractfoxandroid.Address;
import com.example.jakubkalinowski.contractfoxandroid.Contractor;
import com.example.jakubkalinowski.contractfoxandroid.Member;
import com.example.jakubkalinowski.contractfoxandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileEdit.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileEdit extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String param = "kj";
    private static final String TAG = "authListener_TAG!!" ;
    //[Firebase_variable]**
    private FirebaseAuth mAuth; //auth object //add authlistener objects here
    private FirebaseAuth.AuthStateListener mAuthListener; //signed_in state listener object

    private DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance()
            .getReference();

    private OnFragmentInteractionListener mListener;

    private EditText firstName;
    public ProfileEdit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileEdit.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileEdit newInstance(String param1, String param2) {
        ProfileEdit fragment = new ProfileEdit();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //these fields are declared as global variables to be used in callback functions
        //for firebase data retrievl. If possible, find a way for data retrieval to happen
        // before oncreateVIew.
        View root = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        firstName = (EditText)root.findViewById(R.id.firstName_editProfile_Fragment);


        //this is where the magic happens. (data retrieval)
        //the parameter inside gets the current authenticated user's hash STRING value.
        setFirstName(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //firstName.setText(mFirstName_Textbox_Value);
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    public void setFirstName(String uid){


        //there is a callback function inside here. To use outside variables inside the callback
        //functions, the variable inside should be either final or global variable.
        //Still trying to figure out the best way to handle callback functions.
        mFirebaseDatabaseReference
                .child("contractors").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Member m = dataSnapshot.getValue(Contractor.class);
                firstName.setText(m.getEmailAddress());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
