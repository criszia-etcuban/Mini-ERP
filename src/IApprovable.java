public interface IApprovable {
    boolean isApproved();
    void approve(String approverName);
    String getApprovalStatus();
}