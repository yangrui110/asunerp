def file=new File('ddd.bas')
file.eachLine {line->
    line.replace('"DFTH"',"")
}


"""
 If atts(i).TagString = "DFTH" Then
                        th = atts(i).TextString
                    ElseIf atts(i).TagString = "DFTHR" Then
                        thr = atts(i).TextString
                    ElseIf atts(i).TagString = "DFTM1" Then
                        tm1 = atts(i).TextString
                    ElseIf atts(i).TagString = "DFTM2" Then
                        tm2 = atts(i).TextString
                    ElseIf atts(i).TagString = "DFYS" Then
                        ys = atts(i).TextString
                    ElseIf atts(i).TagString = "DFYH" Then
                        yh = atts(i).TextString
                    ElseIf atts(i).TagString = "DFSCALE" Then
                        sca = atts(i).TextString
                    ElseIf atts(i).TagString = "DFWEIGHT" Then
                        weight = atts(i).TextString
                    ElseIf atts(i).TagString = "DFPNAME" Then
                        pname = atts(i).TextString
                    ElseIf atts(i).TagString = "DFMJ" Then
                        mj = atts(i).TextString
                    ElseIf atts(i).TagString = "DFTYPE" Then
                        typ = atts(i).TextString
                    ElseIf atts(i).TagString = "DFWGTH" Then
                        wgth = atts(i).TextString
                    ElseIf atts(i).TagString = "DFFLAG" Then
                        flag = atts(i).TextString
                    ElseIf atts(i).TagString = "DFDETAIL" Then
                        detail = "1"
                        atts(i).TextString = "1"
                    ElseIf atts(i).TagString = "DFAREA" Then
"""