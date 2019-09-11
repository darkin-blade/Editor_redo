echo "\033[1;34mAdding...\033[0m"
if [ ${#1} -le 0 ]
then
  echo "\033[1;33mNeed Message\033[0m"
else
  git add .
  git commit -m $1
    echo "\033[1;32mPushing...\033[0m"
  if [ ${#2} -ge 1 ]
  then
    git push origin master
  else
    echo "\033[1;35mPush Abort\033[0m"
  fi
fi
