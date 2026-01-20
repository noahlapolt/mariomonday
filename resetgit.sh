if [ -z $1 ] ; then
  echo "Provide a branch name!"
  exit 1
fi

git checkout main
git pull
git branch -D $1
git checkout -b $1
git push origin --delete $1
git push --set-upstream origin $1