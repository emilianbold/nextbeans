<?php
$servers = akismet_get_server_connectivity();
$fail_count = count($servers) - count(array_filter($servers));
if (is_array($servers) && count($servers) > 0) {
    // some connections work, some fail
    if ($fail_count > 0 && $fail_count < count($servers)) {
?>
        <p style="padding: .5em; background-color: #aa0; color: #fff; font-weight:bold;"><?php _e('Unable to reach some Akismet servers.'); ?></p>
        <p><?php echo sprintf(__('A network problem or firewall is blocking some connections from your web server to Akismet.com.  Akismet is working but this may cause problems during times of network congestion.  Please contact your web host or firewall administrator and give them <a href="%s" target="_blank">this information about Akismet and firewalls</a>.'), 'http://blog.akismet.com/akismet-hosting-faq/'); ?></p>
        <?php
// all connections fail
    } elseif ($fail_count > 0) {
        ?>
        <p style="padding: .5em; background-color: #d22; color: #fff; font-weight:bold;"><?php _e('Unable to reach any Akismet servers.'); ?></p>
        <p><?php echo sprintf(__('A network problem or firewall is blocking all connections from your web server to Akismet.com.  <strong>Akismet cannot work correctly until this is fixed.</strong>  Please contact your web host or firewall administrator and give them <a href="%s" target="_blank">this information about Akismet and firewalls</a>.'), 'http://blog.akismet.com/akismet-hosting-faq/'); ?></p>
        <?php
// all connections wFork
    } else {
        ?>
        <p style="padding: .5em; background-color: #2d2; color: #fff; font-weight:bold;"><?php _e('All Akismet servers are available.'); ?></p>
        <p><?php _e('Akismet is working correctly.  All servers are accessible.'); ?></p>
    <?php
    }
}
    ?>