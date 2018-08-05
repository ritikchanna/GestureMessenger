package leotik.labs.gesturemessenger.Interface;

public interface DownloadListner {
    void OnDownloadResult(int ResponseCode, Object Response);

    void OnErrorDownloadResult(int ResponseCode);

}
