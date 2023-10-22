---
comments: true
authors:
  - gr007
tags:
  - rev
  - deadface
---
# My Daily Macros

## Description

> Points: 70
>
> Created by: hotstovehove

DEADFACE has gotten hold of the HR departments contact list and has been distributing it with a macro in it. There is a phrase the RE team would like for you to pull out of the macro.

Submit the flag as flag{some_text}.

[Download file](./macros.zip)

## Solution

I did not even know what a xlsm file is. And I as the time of writting this writeup, I am still unware what might it be. I opened it in vim and was able to navigate between files inside it like a folder structure. I started to look for any string with `flag{` in it. and found it in the `xl/vbaProject.bin`.

```
" zip.vim version v32
" Browsing zipfile deadface/rev/macros/HR_List.xlsm
" Select a file with cursor and press ENTER

[Content_Types].xml
_rels/.rels
xl/workbook.xml
xl/_rels/workbook.xml.rels
xl/worksheets/sheet1.xml
xl/theme/theme1.xml
xl/styles.xml
xl/sharedStrings.xml
xl/vbaProject.bin
docProps/core.xml
docProps/app.xml
```

![flag](./flag.png)

flag: `flag{youll_never_find_this_}`
