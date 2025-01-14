#!/bin/bash
set -m

host=${CLAMD_HOST:-192.168.50.72}
port=${CLAMD_PORT:-3310}
filesize=${CLAMD_MAX_FILE_SIZE:-5000MB}
timeout=${CLAMD_TIMOUT:-30000}

echo "using clamd server: $host:$port"

# start in background
#java -jar /var/clamav-rest/clamav-rest-1.0.2.jar --clamd.host=$host --clamd.port=$port
java -Xmx256m -jar ./clamav-rest-1.0.2.jar --clamd.host=$host --clamd.port=$port --clamd.maxfilesize=$filesize --clamd.maxrequestsize=$filesize --clamd.timeout=$timeout
