Sub FormatTestTables()
'
' Макрос для форматирования таблиц тестирования
' Создан для отчета по системе управления складом отходов
'

    Dim ws As Worksheet
    Dim lastRow As Long
    Dim currentRow As Long
    Dim tableStartRow As Long
    Dim rng As Range
    
    Set ws = ActiveSheet
    lastRow = ws.Cells(ws.Rows.Count, "A").End(xlUp).Row
    
    ' Применяем общие настройки
    With ws
        .Cells.Font.Name = "Times New Roman"
        .Cells.Font.Size = 12
        .Columns("A:E").ColumnWidth = 20
        .Columns("A").ColumnWidth = 35
    End With
    
    ' Проходим по всем строкам и форматируем таблицы
    currentRow = 1
    
    Do While currentRow <= lastRow
        ' Проверяем, начинается ли строка с "Таблица"
        If Left(ws.Cells(currentRow, 1).Value, 7) = "Таблица" Then
            tableStartRow = currentRow
            
            ' Форматируем заголовок таблицы (Таблица N – ...)
            With ws.Range(ws.Cells(currentRow, 1), ws.Cells(currentRow, 5))
                .Merge
                .Font.Bold = True
                .Font.Size = 12
                .HorizontalAlignment = xlLeft
                .VerticalAlignment = xlCenter
                .WrapText = True
                With .Borders
                    .LineStyle = xlContinuous
                    .Weight = xlThin
                End With
                .Borders(xlEdgeTop).Weight = xlMedium
                .Borders(xlEdgeLeft).Weight = xlMedium
                .Borders(xlEdgeRight).Weight = xlMedium
                .RowHeight = 30
            End With
            currentRow = currentRow + 1
            
            ' Форматируем строку "Название:"
            With ws.Cells(currentRow, 1)
                .Font.Bold = True
                .Interior.Color = RGB(217, 217, 217)
                .HorizontalAlignment = xlLeft
                .VerticalAlignment = xlCenter
                With .Borders
                    .LineStyle = xlContinuous
                    .Weight = xlThin
                End With
                .Borders(xlEdgeLeft).Weight = xlMedium
            End With
            With ws.Range(ws.Cells(currentRow, 2), ws.Cells(currentRow, 5))
                .Merge
                .WrapText = True
                .VerticalAlignment = xlCenter
                With .Borders
                    .LineStyle = xlContinuous
                    .Weight = xlThin
                End With
                .Borders(xlEdgeRight).Weight = xlMedium
            End With
            currentRow = currentRow + 1
            
            ' Форматируем строку "Функция:"
            With ws.Cells(currentRow, 1)
                .Font.Bold = True
                .Interior.Color = RGB(217, 217, 217)
                .HorizontalAlignment = xlLeft
                .VerticalAlignment = xlCenter
                With .Borders
                    .LineStyle = xlContinuous
                    .Weight = xlThin
                End With
                .Borders(xlEdgeLeft).Weight = xlMedium
            End With
            With ws.Range(ws.Cells(currentRow, 2), ws.Cells(currentRow, 5))
                .Merge
                .VerticalAlignment = xlCenter
                With .Borders
                    .LineStyle = xlContinuous
                    .Weight = xlThin
                End With
                .Borders(xlEdgeRight).Weight = xlMedium
            End With
            currentRow = currentRow + 1
            
            ' Форматируем заголовки столбцов (Действие, Ожидаемый результат, Результат теста)
            Dim j As Integer
            For j = 1 To 3
                With ws.Cells(currentRow, j)
                    .Font.Bold = True
                    .Interior.Color = RGB(217, 217, 217)
                    .HorizontalAlignment = xlCenter
                    .VerticalAlignment = xlCenter
                    .WrapText = True
                    With .Borders
                        .LineStyle = xlContinuous
                        .Weight = xlThin
                    End With
                    If j = 1 Then .Borders(xlEdgeLeft).Weight = xlMedium
                End With
            Next j
            With ws.Range(ws.Cells(currentRow, 4), ws.Cells(currentRow, 5))
                .Merge
                .Font.Bold = True
                .Interior.Color = RGB(217, 217, 217)
                .HorizontalAlignment = xlCenter
                .VerticalAlignment = xlCenter
                .WrapText = True
                With .Borders
                    .LineStyle = xlContinuous
                    .Weight = xlThin
                End With
                .Borders(xlEdgeRight).Weight = xlMedium
            End With
            ws.Rows(currentRow).RowHeight = 30
            currentRow = currentRow + 1
            
            ' Форматируем строки с результатами теста (• пройден, • провален, • заблокирован)
            Dim i As Integer
            For i = 1 To 3
                For j = 1 To 3
                    With ws.Cells(currentRow, j)
                        With .Borders
                            .LineStyle = xlContinuous
                            .Weight = xlThin
                        End With
                        If j = 1 Then .Borders(xlEdgeLeft).Weight = xlMedium
                    End With
                Next j
                With ws.Range(ws.Cells(currentRow, 4), ws.Cells(currentRow, 5))
                    .Merge
                    .VerticalAlignment = xlCenter
                    With .Borders
                        .LineStyle = xlContinuous
                        .Weight = xlThin
                    End With
                    .Borders(xlEdgeRight).Weight = xlMedium
                End With
                currentRow = currentRow + 1
            Next i
            
            ' Форматируем остальные строки таблицы до следующей таблицы или пустой строки
            Do While currentRow <= lastRow And ws.Cells(currentRow, 1).Value <> "" And Left(ws.Cells(currentRow, 1).Value, 7) <> "Таблица"
                ' Проверяем, является ли строка заголовком раздела (Предусловие, Шаги теста, Постусловие)
                If ws.Cells(currentRow, 1).Value = "Предусловие:" Or _
                   ws.Cells(currentRow, 1).Value = "Шаги теста:" Or _
                   ws.Cells(currentRow, 1).Value = "Постусловие:" Then
                    With ws.Range(ws.Cells(currentRow, 1), ws.Cells(currentRow, 5))
                        .Merge
                        .Font.Bold = True
                        .Interior.Color = RGB(217, 217, 217)
                        .HorizontalAlignment = xlLeft
                        .VerticalAlignment = xlCenter
                        With .Borders
                            .LineStyle = xlContinuous
                            .Weight = xlThin
                        End With
                        .Borders(xlEdgeLeft).Weight = xlMedium
                        .Borders(xlEdgeRight).Weight = xlMedium
                    End With
                Else
                    ' Обычные строки данных
                    For j = 1 To 3
                        With ws.Cells(currentRow, j)
                            .WrapText = True
                            .VerticalAlignment = xlTop
                            With .Borders
                                .LineStyle = xlContinuous
                                .Weight = xlThin
                            End With
                            If j = 1 Then .Borders(xlEdgeLeft).Weight = xlMedium
                            If j = 3 Then .HorizontalAlignment = xlCenter
                        End With
                    Next j
                    With ws.Range(ws.Cells(currentRow, 4), ws.Cells(currentRow, 5))
                        .Merge
                        .VerticalAlignment = xlTop
                        With .Borders
                            .LineStyle = xlContinuous
                            .Weight = xlThin
                        End With
                        .Borders(xlEdgeRight).Weight = xlMedium
                    End With
                    
                    ' Устанавливаем автоматическую высоту строки
                    ws.Rows(currentRow).AutoFit
                End If
                currentRow = currentRow + 1
            Loop
            
            ' Добавляем нижнюю границу таблицы
            With ws.Range(ws.Cells(currentRow - 1, 1), ws.Cells(currentRow - 1, 5))
                .Borders(xlEdgeBottom).LineStyle = xlContinuous
                .Borders(xlEdgeBottom).Weight = xlMedium
            End With
            
        Else
            currentRow = currentRow + 1
        End If
    Loop
    
    ' Устанавливаем ориентацию страницы
    With ws.PageSetup
        .Orientation = xlPortrait
        .PaperSize = xlPaperA4
        .Zoom = False
        .FitToPagesWide = 1
        .FitToPagesTall = False
        .LeftMargin = Application.CentimetersToPoints(2)
        .RightMargin = Application.CentimetersToPoints(1.5)
        .TopMargin = Application.CentimetersToPoints(2)
        .BottomMargin = Application.CentimetersToPoints(2)
    End With
    
    MsgBox "Форматирование таблиц завершено!", vbInformation, "Готово"
    
End Sub
