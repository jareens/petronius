> <h1>Garment choice parameters</h1>

> <h3><a href='#introduction'>1 Introduction</a></h3>
> <h3><a href='#parameters'>2 Parameters</a></h3>
> <h3><a href='#evaluation'>3 How parameters are evaluated</a></h3>
> <h3><a href='#table_of_values'>4 Table of values</a></h3>

> <h3><a>1 Introduction</a></h3>
> <p>
<blockquote>This page shows the list of the parameters computed for the garment<br>
choice.  Every parameter contributes to provide a score to the garment,<br>
the garment with highest score is proposed in the selection.<br>
</blockquote><blockquote></p>
<h3><a>2 Parameters</a></h3>
<p>
<blockquote>The meanings and values of the paramters are explained below:<br>
<h5>2.1 Preference</h5>
It's computed according to the grade assigned to the garment.<br>
<h5>2.2 Date of purchase</h5>
The newer is the garment, the higher score it gets.<br>
<h5>2.3 Last selection</h5>
If the garment was recently selected it gets a low score.<br>
<h5>2.4 Selection count</h5>
It depends on how many times the garment was selected in the last 30<br>
days. If it was selected often it gets a low score.<br>
<h5>2.5 Random</h5>
A random value to let the coiche be not deterministic.<br>
</blockquote></p>
<h3><a>3 How parameters are evaluated</a></h3>
<p>
<blockquote>Every parameter has an absolute value and a weighted one; the first is<br>
computed referring to the constraints explained above, the second is<br>
taken from the first, multiplied by a certain coefficient according to the<br>
importance of the parameter in the overall count.<br>
</blockquote></p>
<p>
<blockquote>Every parameter must be contained within a range stated by its maximum a<br>
minimun values.<br>
</blockquote></p>
<p>
<blockquote>The coefficient used to get the weighed value can be proportional or<br>
logarithmic; in the first case there will be no relative difference with<br>
the absolute value of the parameter, in the second case the computed<br>
value will be more-than-proportionally higher or lower in relation to the<br>
absolute value itself.<br>
</blockquote></p>
<p>
<blockquote>The parameter can also be positive or negative, note that in case of<br>
logarithmic negative parameters the porportional relation explained above<br>
is inverted.<br>
</blockquote></p>
<h3><a>4 Table of values</a></h3>
<p>
<blockquote>This is the summary table of parameter properties:<br>
</blockquote></p>
<p>
<blockquote><table border='1'>
<blockquote><tr>
<blockquote><td>
<blockquote>Parameter<br>
</blockquote></td>
<td>
<blockquote>+/-<br>
</blockquote></td>
<td>
<blockquote>Maximum<br>
</blockquote></td>
<td>
<blockquote>Minimum<br>
</blockquote></td>
<td>
<blockquote>Weight<br>
</blockquote></td>
<td>
<blockquote>Proportionality<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>
<blockquote><b>Preference</b>
</blockquote></td>
<td>
<blockquote>Positive<br>
</blockquote></td>
<td>
<blockquote>0<br>
</blockquote></td>
<td>
<ol><li>
</li></ol></td>
<td>
<ol><li>1/2<br>
</li></ol></td>
<td>
<blockquote>Logarithmic<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>
<blockquote><b>Date of purchase</b>
</blockquote></td>
<td>
<blockquote>Negative<br>
</blockquote></td>
<td>
<blockquote>0<br>
</blockquote></td>
<td>
<ol><li>
</li></ol></td>
<td>
<ol><li>4<br>
</li></ol></td>
<td>
<blockquote>Logarithmic<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>
<blockquote><b>Last selection</b>
</blockquote></td>
<td>
<blockquote>Positive<br>
</blockquote></td>
<td>
<blockquote>0<br>
</blockquote></td>
<td>
<blockquote>30<br>
</blockquote></td>
<td>
<ol><li>1/2<br>
</li></ol></td>
<td>
<blockquote>Logarithmic<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>
<blockquote><b>Selection count</b>
</blockquote></td>
<td>
<blockquote>Negative<br>
</blockquote></td>
<td>
<blockquote>0<br>
</blockquote></td>
<td>
<blockquote>30<br>
</blockquote></td>
<td>
<blockquote>2<br>
</blockquote></td>
<td>
<blockquote>Proportional<br>
</blockquote></td>
</blockquote></tr>
<tr>
<blockquote><td>
<blockquote><b>Random</b>
</blockquote></td>
<td>
<blockquote>Positive<br>
</blockquote></td>
<td>
<blockquote>0<br>
</blockquote></td>
<td>
<ol><li>0<br>
</li></ol></td>
<td>
<ol><li>2<br>
</li></ol></td>
<td>
<blockquote>Proportional<br>
</blockquote></td>
</blockquote></tr>
</blockquote></table>
</blockquote></p>