   M S B A M L   `   `   `  ����M  IFaceTrackingBasics, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null7 clr-namespace:FaceTrackingBasicsFaceTrackingBasics  5   	   #    FaceTrackingBasics.MainWindow   5   	   W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35\ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35D 9http://schemas.microsoft.com/winfx/2006/xaml/presentation    5   	   R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5   	   2local clr-namespace:FaceTrackingBasics     5   	   -   5          � Title$  Face Tracking Basics��5   	   $	��640��5	   	   $	��480��5
   	   .��5      � -   5        $��MainGrid��5      $	��640��5      $	��480��6   .+�5   
   �� -   5        $��
ColorImage��5      +   %FaceTrackingBasics.FaceTrackingViewer  5   
   -   5          Name$ faceTrackingViewer��5   #   �� 5   
   -   5        $��
rectangle1��5   N   $��23��6   $
��Left=�6    $��	7,371,0,0q�6;   
���   �$	��Top=�6o   $	��138��6�   
�������6�   �� 5   
   -   5        $��
rectangle2��5   O   $	��101��6   $
��Left=�6!   $��	7,264,0,0q�6<   
���   �$	��Top=�6p   $	��139��6�   
�������6�   �� 5   
   -   5        $��button1��5   a   $��	DepthInit��6   $��23��6%   $
��Left=�61   $��512,373,0,0q�6L   $	��Top=�6p   $��75��6�   6�   �� 5   
   -   5        $��textBlockDepth��5   O   $��23��6   $
��Left=�6    $��
11,373,0,0q�6;   $	��Top=�6�   $	��131��6�   ��6e   ��  �� ElementName$ slider1�� �� Path$ Value�6�   �� 5   
   -	   5        $��slider1��5   K   $��23��6   $
��Left=�6   $��	7,402,0,0q�68   $	��Top=�6Z   $	��138��6r    � Value$
 0.70Q�6~    � Maximum$ 1Q�6�    � Minimum$	 0.6Q�6�   �� 5   
   -
   5        $��checkBox_face��5   ]   $��Send face info��6   $
��Left=�6,   $��16,271,0,189q�6G   $	��122��6r    d� 	IsChecked$
 TrueY�6~   �� 5   
   -   5        $��checkBox_audio��5   h   $��Send audio info��6   $��20��6-   $
��Left=�69   $��
16,0,0,141q�6T   $��Bottom=�6~   $	��122��6�   {�. $
 TrueY�6�   �� 5   
   -   5        $��checkBox_movewindow��5   d   $��Move Window��6   $��20��6)   $
��Left=�65   $��
16,296,0,0q�6P   $	��Top=�6   $	��122��6�   $
 TrueY�6�   �� 5   
   -   5        $��checkBox_fixYdegree��5   v   $��fix Y degree��6   $��20��6*   $
��Left=�66   {�. $��
16,0,0,115q�6b   $��Bottom=�6�   $	��122��6�   �� 5   
   -   5        $��
labelDepth��5   \   $��Label��6   $��28��6    $
��Left=�6,   $��530,412,0,0q�6G   $	��Top=�6n   5      5      