#!/bin/sh -x

# The contents of this file are subject to the terms of the Common Development
# and Distribution License (the License). You may not use this file except in
# compliance with the License.

# You can obtain a copy of the License at http://www.netbeans.org/cddl.html
# or http://www.netbeans.org/cddl.txt.

# When distributing Covered Code, include this CDDL Header Notice in each file
# and include the License file at http://www.netbeans.org/cddl.txt.
# If applicable, add the following below the CDDL Header, with the fields
# enclosed by brackets [] replaced by your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"

# The Original Software is NetBeans. The Initial Developer of the Original
# Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
# Microsystems, Inc. All Rights Reserved.

chown_dir=$1
unpack_dir=$2
jdk_home=$3
set -e

echo Changing ownership for $chown_dir
#Fix for 177872 (alternative: change Auth="None" for GF, but problem with existing root:admin dirs)
ownership=`ls -nlda ~ | awk ' { print $3 ":admin" } ' 2>/dev/null`
chown -R "$ownership" "$chown_dir"

echo Calling unpack200 in $unpack_dir
cd "$unpack_dir"
for x in `find . -name \*.jar.pack` ; do
    jar=`echo $x | sed 's/jar.pack/jar/'`
    if [ -f "$jar" ] ; then
        continue
    fi
    "$jdk_home/bin/unpack200" $x $jar
    chmod `stat -f %Lp $x` $jar && touch -r $x $jar
    chown "$ownership" "$jar"
    rm $x
done

exit 0
