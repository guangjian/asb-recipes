#!/bin/bash
/usr/bin/sar -u 1 1 | tail -1 | awk '{print $3}'
