<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, OPTIONS");
header("Content-Type: text/plain");

if (!isset($_GET['path'])) {
    echo "";
    exit;
}

$path = basename($_GET['path']);
$filepath = __DIR__ . '/uploads/' . $path;

if (file_exists($filepath)) {
    $type = pathinfo($filepath, PATHINFO_EXTENSION);
    $data = file_get_contents($filepath);
    $base64 = 'data:image/' . $type . ';base64,' . base64_encode($data);
    echo $base64;
} else {
    echo "";
}
?>
