<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import '@/common/design-tokens.css';

onLaunch(() => {
  console.log("App Launch");
});
onShow(() => {
  console.log("App Show");
});
onHide(() => {
  console.log("App Hide");
});
</script>
<style>
page {
  background: var(--color-bg, #E8E0CF);
  font-family: var(--font-family, "PingFang SC", "Helvetica Neue", Helvetica, sans-serif);
}
</style>
