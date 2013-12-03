# -*- coding: cp936 -*-
'''
注意当测试集，训练集不大时，在测试集中出现的单词在训练集中有可能不存在，这种情况直接忽略这个词的判断。
'''
import os
import random

textNum = 0
wordNum = 0
ctgyNum = 0
weight = [[0 for x in range(ctgyNum)] for y in range(wordNum)]
category = ['finance','local','computer','house','edu','tech','car','talent','sport','healthy','artist','fun']
words = set([])


def process(inPath,outPath,ctgy,fileName,isTrainData):
    text=open(inPath+'\\'+fileName)
    lines = text.readlines()
    wf = {}
    cnt = 0
    for line in lines:
        arr = line.split()
        for w in arr:
            w = w.strip()
            if wf.has_key(w):
                wf[w]+=1
            else:
                wf[w] = 1
                cnt+=1
            if isTrainData:             #只收集训练集中的单词
                if w not in words:
                    words.add(w)
    for (k,v) in wf.items():
        wf[k]/=float(cnt)
    text2 = open(outPath+'\\'+ctgy+fileName,'w')
    for (k,v) in wf.items():
        text2.write(k+'\t'+str(v)+'\n')
    text.close()
    text2.close()
    
def wordFreq():
    print "计算中，请稍后..."
    path = "F:\\IT\\workspace\\maxEntClassify\\data\\TanCorp-12-Txt"
    trainPath ="F:\\IT\\workspace\\maxEntClassify\\data\\train\\"
    testPath ="F:\\IT\\workspace\\maxEntClassify\\data\\test\\"
    wordPath = "F:\\IT\\workspace\\maxEntClassify\\data\\words.txt"
    dirs = os.listdir(path)
    trainCnt = 0
    testCnt = 0
    for ctgy in dirs:
        currPath = path+'\\'+ctgy
        files = os.listdir(currPath)
        index = 0                   #为了减小计算量，每类别最多取200个数据
        for f in files:
            index+=1
            if index>300 : break
            if random.random()>0.2:
                process(currPath,trainPath,ctgy,f,True)
                trainCnt+=1
            else :
                process(currPath,testPath,ctgy,f,False)
                testCnt+=1    
    stat = open(wordPath,'w')
    for word in words:
        stat.write(word+'\n')
    stat.close()
    print "处理完毕："
    print "单词总量"+str(len(words))
    print "训练总量"+str(trainCnt)
    print "测试总量"+str(testCnt)

if __name__ == '__main__' :
    wordFreq()
   
