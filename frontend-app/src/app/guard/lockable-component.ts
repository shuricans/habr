export default interface LockableComponent {
  allowRedirect: boolean;
  canDeactivate(): boolean;
}