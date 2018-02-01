#!/bin/bash
set -m

host=${CLAMD_HOST:-192.168.50.72}
port=${CLAMD_PORT:-3310}
timeout=${CLAMD_TIMOUT:-30000}
max_file_size=${CLAMD_MAX_FILE_SIZE:-5000MB}
max_request_size=${CLAMD_MAX_FILE_SIZE:-5000MB}

echo "using clamd server: $host:$port"

# start in background
java -Xmx256m -jar ./clamav-rest-1.0.2.jar --clamd.host=$host --clamd.port=$port  --clamd.timeout=$timeout --clamd.maxfilesize=$max_file_size  --clamd.maxrequestsize=$max_request_size
