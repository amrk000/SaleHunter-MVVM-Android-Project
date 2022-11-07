package amrk000.salehunter.util;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.HashSet;

import amrk000.salehunter.R;
import amrk000.salehunter.model.SortAndFilterModel;
import amrk000.salehunter.view.fragment.dialogs.DisconnectedDialog;
import amrk000.salehunter.view.fragment.dialogs.EmailVerificationDialog;
import amrk000.salehunter.view.fragment.dialogs.LoadingDialog;
import amrk000.salehunter.view.fragment.dialogs.MessageDialog;
import amrk000.salehunter.view.fragment.dialogs.PasswordChangeDialog;
import amrk000.salehunter.view.fragment.dialogs.SortAndFilterDialog;

public class DialogsProvider {
    private static DialogsProvider instance;
    private static FragmentManager fragmentManager;

    // App Dialogs
    private LoadingDialog loadingDialog;
    private DisconnectedDialog disconnectedDialog;
    private MessageDialog messageDialog;
    private EmailVerificationDialog emailVerificationDialog;
    private PasswordChangeDialog passwordChangeDialog;
    private SortAndFilterDialog sortAndFilterDialog;

    public DialogsProvider(){
        loadingDialog = new LoadingDialog();
        disconnectedDialog = new DisconnectedDialog();
        messageDialog = new MessageDialog();
        emailVerificationDialog = new EmailVerificationDialog();
        passwordChangeDialog = new PasswordChangeDialog();
        sortAndFilterDialog = new SortAndFilterDialog();
    }

    public static DialogsProvider get(Activity activity){
        fragmentManager = ((AppCompatActivity)activity).getSupportFragmentManager();

        if(instance == null) instance = new DialogsProvider();
        return instance;
    }

    public void setLoading(boolean loading){
        if(loading && !loadingDialog.isVisible()) loadingDialog.show(fragmentManager, loadingDialog.getTag());
        else if(loadingDialog.isVisible()) loadingDialog.dismiss();
    }

    public void setDisconnected(boolean disconnected){
        if(disconnected) disconnectedDialog.show(fragmentManager, disconnectedDialog.getTag());
        else if(disconnectedDialog.isVisible()) disconnectedDialog.dismiss();
    }

    public void messageDialog(String title, String subTitle){
        if(messageDialog.isVisible()) messageDialog.dismiss();
        else {
            messageDialog.setMessage(title, subTitle);
            messageDialog.show(fragmentManager, messageDialog.getTag());
        }
    }

    public void emailVerificationDialog(String email, String title, String subTitle, EmailVerificationDialog.DialogResultListener dialogResultListener){
        if (emailVerificationDialog.isVisible()) emailVerificationDialog.dismiss();
        else {
            emailVerificationDialog.setEmail(email);
            emailVerificationDialog.setMessage(title,subTitle);
            emailVerificationDialog.setDialogResultListener(dialogResultListener);

            emailVerificationDialog.show(fragmentManager, emailVerificationDialog.getTag());
        }
    }

    public void passwordChangeDialog(){
        if (passwordChangeDialog.isVisible()) passwordChangeDialog.dismiss();
        else passwordChangeDialog.show(fragmentManager, passwordChangeDialog.getTag());
    }

    public void sortAndFilterDialog(SortAndFilterModel sortAndFilterModel, HashSet<String> categories, HashSet<String> brands, SortAndFilterDialog.DialogResultListener dialogResultListener){
        if (sortAndFilterDialog.isVisible()) sortAndFilterDialog.dismiss();
        else {
            sortAndFilterDialog.setSortAndFilterModel(sortAndFilterModel);
            sortAndFilterDialog.setDialogResultListener(dialogResultListener);
            sortAndFilterDialog.setCategories(categories);
            sortAndFilterDialog.setBrands(brands);
            sortAndFilterDialog.show(fragmentManager, sortAndFilterDialog.getTag());
        }
    }

}
